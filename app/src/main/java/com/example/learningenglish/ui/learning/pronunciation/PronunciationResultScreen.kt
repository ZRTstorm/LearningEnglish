package com.example.learningenglish.ui.learning.pronunciation

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.core.net.toUri
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationResultScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    contentId: Int,
    contentsType: String,
) {
    val evalResult = viewModel.evalResult.collectAsState().value
    val startResult = viewModel.startResult.collectAsState().value
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var mediaPlayer = remember { MediaPlayer() }
    var selectedVoice by remember { mutableStateOf("US") }
    var expandedMetric by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("평가 결과") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (evalResult == null || startResult == null) {
                CircularProgressIndicator()
                return@Column
            }

            Text("📌 평가한 문장:", style = MaterialTheme.typography.labelLarge)
            Text(startResult.sentence, style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("US", "GB", "AU").forEach { voice ->
                    Button(
                        onClick = { selectedVoice = voice },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedVoice == voice) Color(0xFF6D9886) else Color(0xFFE0E0E0),
                            contentColor = if (selectedVoice == voice) Color.White else Color.Black
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(voice)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                val iconBackground = Modifier
                    .size(48.dp)
                    .background(Color(0xFFDDE6E4), CircleShape)
                IconButton(onClick = {
                    val fileName = when (selectedVoice) {
                        "GB" -> startResult.ttsContents.firstOrNull()?.filePathGb
                        "AU" -> startResult.ttsContents.firstOrNull()?.filePathAu
                        else -> startResult.ttsContents.firstOrNull()?.filePathUs
                    } ?: return@IconButton
                    coroutineScope.launch {
                        val file = viewModel.getAudioFileFromFilename(context, fileName)
                        file?.let {
                            try {
                                mediaPlayer.reset()
                                mediaPlayer.setDataSource(context, it.toUri())
                                mediaPlayer.prepare()
                                mediaPlayer.start()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }, modifier = iconBackground) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "재생", tint = Color(0xFF5F6368))
                }
                IconButton(onClick = {
                    if (mediaPlayer.isPlaying) mediaPlayer.pause()
                }, modifier = iconBackground) {
                    Icon(Icons.Default.Pause, contentDescription = "일시정지", tint = Color(0xFF5F6368))
                }
                IconButton(onClick = {
                    try {
                        mediaPlayer.seekTo(0)
                        mediaPlayer.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, modifier = iconBackground) {
                    Icon(Icons.Default.Refresh, contentDescription = "반복재생", tint = Color(0xFF5F6368))
                }
            }

            val metrics = listOf(
                Triple("정확도", evalResult.accuracy, "발음이 정확했는가"),
                Triple("유창성", evalResult.fluency, "말이 끊김 없이 자연스럽게 이어졌는가"),
                Triple("완성도", evalResult.completeness, "단어를 모두 발음했는가"),
                Triple("총점", evalResult.pronunciation, "전체 종합 평가 점수")
            )

            metrics.forEach { (label, value, description) ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF2F2F2), RoundedCornerShape(10.dp))
                        .clickable { expandedMetric = if (expandedMetric == label) null else label }
                        .padding(16.dp)
                ) {
                    Text("$label", style = MaterialTheme.typography.titleMedium)
                    LinearProgressIndicator(
                        progress = value.toFloat() / 100f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .padding(top = 8.dp),
                        color = Color(0xFF6D9886)
                    )
                    Text("${value.toInt()}점", style = MaterialTheme.typography.bodySmall)

                    if (expandedMetric == label) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(description, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Text("💬 피드백:", style = MaterialTheme.typography.labelLarge)
            val feedbacks = evalResult.feedbackMessages ?: emptyList()
            feedbacks.take(3).forEach { msg ->
                Text("- $msg")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { navController.navigate("pronunciation_sentence_type/$contentsType/$contentId") },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6D9886))
                ) {
                    Text("새로운 문제로 학습하기", color = Color.White)
                }

                Button(
                    onClick = { navController.popBackStack() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
                ) {
                    Text("다시 시도하기", color = Color.Black)
                }
            }
        }
    }
}
