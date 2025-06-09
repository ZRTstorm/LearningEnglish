package com.example.learningenglish.ui.learning.dictation

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.learningenglish.data.model.DictationEvalRequest
import com.example.learningenglish.data.model.DictationResultData
import com.example.learningenglish.data.model.DictationStartRequest
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun DictationScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    contentId: Int,
    contentsType: String,
    sentenceLevel: Int,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userPrefs = UserPreferencesDataStore(context)
    var userId by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().first() ?: 0
    }

    var sentenceText by remember { mutableStateOf("") }
    var userInput by remember { mutableStateOf("") }
    var sentenceId by remember { mutableStateOf(-1) }
    var isLoading by remember { mutableStateOf(true) }

    var filePaths by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var selectedVoice by remember { mutableStateOf("US") }
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer = remember { MediaPlayer() }


    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(Unit) {
        val result = viewModel.startDictation(
            DictationStartRequest(userId,contentId, contentsType, sentenceLevel)
        )
        Log.d("DictationDebug", "📦 응답 결과: $result")
        if (result == null) {
            Log.e("DictationAPI", "❌ 응답이 null입니다.")
        } else {
            sentenceId = result.sentenceId
            sentenceText = result.text
            filePaths = mapOf(
                "US" to result.contents.firstOrNull()?.filePathUs.orEmpty(),
                "GB" to result.contents.firstOrNull()?.filePathGb.orEmpty(),
                "AU" to result.contents.firstOrNull()?.filePathAu.orEmpty()
            )
            Log.d("DictationDebug", "✅ sentenceId=$sentenceId, filePaths=$filePaths")
        }
        isLoading = false
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                "받아쓰기 문장을 듣고 따라 적어보세요!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF333333)
            )
            Spacer(Modifier.height(24.dp))

            // 🎧 음성 선택 버튼
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("US", "GB", "AU").forEach { voice ->
                    Button(
                        onClick = { selectedVoice = voice },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedVoice == voice) Color(0xFF6D9886) else Color(
                                0xFFE0E0E0
                            ),
                            contentColor = if (selectedVoice == voice) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) { Text(voice) }
                }
            }

            // ▶️ 재생 / 일시정지 / 반복 버튼
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val iconBackground = Modifier
                    .size(48.dp)
                    .background(Color(0xFFDDE6E4), CircleShape)
                IconButton(onClick = {
                    val fileName = filePaths[selectedVoice] ?: return@IconButton
                    coroutineScope.launch {
                        val file = viewModel.getAudioFileFromFilename(context, fileName)
                        file?.let {
                            try {
                                mediaPlayer.reset()
                                mediaPlayer.setDataSource(context, it.toUri())
                                mediaPlayer.prepare()
                                mediaPlayer.start()
                                isPlaying = true
                            } catch (e: Exception) {
                                Log.e("AudioDebug", "재생 실패: ${e.localizedMessage}")
                            }
                        } ?: Log.e("AudioDebug", "파일 다운로드 실패")
                    }
                }) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "재생",
                        tint = Color(0xFF5F6368)
                    )
                }
                IconButton(onClick = {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    }
                }, modifier = iconBackground) {
                    Icon(Icons.Default.Pause, contentDescription = "일시정지", tint = Color(0xFF5F6368))
                }
                IconButton(onClick = {
                    try {
                        mediaPlayer.seekTo(0)
                        mediaPlayer.start()
                        isPlaying = true
                    } catch (e: Exception) {
                        Log.e("MediaPlayer", "반복 실패: ${e.localizedMessage}")
                    }
                }, modifier = iconBackground) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "반복재생",
                        tint = Color(0xFF5F6368)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                label = { Text("내가 쓴 문장") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val evalResult = viewModel.evaluateDictation(
                            DictationEvalRequest(
                                sentenceId = sentenceId,
                                userText = userInput,
                                userId = userId,
                                contentType = contentsType,
                                contentId = contentId
                            )
                        )
                        evalResult?.let {
                            viewModel.lastDictationResult = DictationResultData(
                                reference = it.reference,
                                userInput = it.userInput,
                                accuracyScore = it.accuracyScore,
                                grammarScore = it.grammarScore,
                                incorrectWords = it.incorrectWords,
                                feedbackMessages = it.feedbackMessages,
                                contentId = contentId,
                                contentsType = contentsType,
                                filePaths = filePaths
                            )
                            navController.navigate("dictation_result")
                        }
                    }
                },
                modifier = Modifier.align(Alignment.End),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D9886))
            ) {
                Text("제출 및 채점", color = Color.White)
            }

        }}}