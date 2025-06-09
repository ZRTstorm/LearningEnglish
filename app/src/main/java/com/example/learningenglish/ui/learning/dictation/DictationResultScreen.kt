package com.example.learningenglish.ui.learning.dictation

import android.media.MediaPlayer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch
import kotlin.math.min


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictationResultScreen(
    navController: NavController,
    reference: String,
    userInput: String,
    score: Double,
    grammarScore: Double,
    incorrectWords: List<String>,
    feedbackMessages: List<String>,
    contentId: Int,
    contentsType: String,
    filePaths: Map<String, String>,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedVoice by remember { mutableStateOf("US") }
    var mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("채점 결과") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            DictationScoreChart(score)

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
                    val fileName = filePaths[selectedVoice] ?: return@IconButton
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
                    Icon(Icons.Default.Refresh, contentDescription = "반복", tint = Color(0xFF5F6368))
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("📌 정답 문장:", style = MaterialTheme.typography.labelLarge)
            Text(reference, style = MaterialTheme.typography.bodyMedium)



            Text("📝 내가 쓴 문장:", style = MaterialTheme.typography.labelLarge)
            Text(userInput, style = MaterialTheme.typography.bodyMedium)



            Text("❌ 틀린 단어:", style = MaterialTheme.typography.labelLarge)
            Text(incorrectWords.joinToString(", "), style = MaterialTheme.typography.bodyMedium)

            Text("💬 피드백:", style = MaterialTheme.typography.labelLarge)
            feedbackMessages.forEach { feedback ->
                Text("• $feedback", style = MaterialTheme.typography.bodyMedium)
            }

            Text("📖 문법 점수: ${"%.2f".format(grammarScore * 100)}", style = MaterialTheme.typography.bodyMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { navController.navigate("dictation_sentence_type/$contentsType/$contentId") },
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

@Composable
fun DictationScoreChart(
    score: Double,
    modifier: Modifier = Modifier
) {
    val correctAngle = (score.coerceIn(0.0, 100.0) / 100.0 * 360f).toFloat()
    val incorrectAngle = 360f - correctAngle

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 24.dp.toPx()
            val sizeOffset = strokeWidth / 2
            val arcSize = size.copy(width = size.width - strokeWidth, height = size.height - strokeWidth)

            // 오답 (배경)
            drawArc(
                color = Color(0xFFFF7043),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(sizeOffset, sizeOffset),
                size = arcSize
            )

            // 정답
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = -90f,
                sweepAngle = correctAngle,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(sizeOffset, sizeOffset),
                size = arcSize
            )
        }

        // 중앙 텍스트
        Text(
            text = "✅정확도 : ${"%.2f".format(score)}%",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

