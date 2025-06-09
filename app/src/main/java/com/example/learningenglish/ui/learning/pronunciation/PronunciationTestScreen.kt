package com.example.learningenglish.ui.learning.pronunciation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationTestScreen(
    viewModel: LearningViewModel,
    contentId: Int,
    contentsType: String,
    navController: NavController
) {
    /*
    val context = LocalContext.current
    val audioRecorder = remember(context) { AudioRecorder() }

    val textDetail by viewModel.textDetail.collectAsState()
    val selectedSentence by viewModel.selectedEvalSentence.collectAsState()
    val result by viewModel.evalResult.collectAsState()

    var isRecording by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }

    // 첫 문장 자동 선택
    LaunchedEffect(contentId) {
        viewModel.loadTextDetail(contentId)
        val first = viewModel.textDetail.value?.textFiles?.firstOrNull()?.sentences?.firstOrNull()?.originalText
        first?.let { viewModel.setEvalSentence(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🎤 발음 평가") })
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        if (!isRecording) {
                            audioFile = audioRecorder.startRecording(context)
                            isRecording = true
                        } else {
                            val resultFile = audioRecorder.stopRecording()
                            if (resultFile != null && selectedSentence != null) {
                                audioFile = resultFile
                                viewModel.evaluatePronunciation(contentId, selectedSentence!!, resultFile)
                            }
                            isRecording = false
                        }
                    }) {
                        Text(if (isRecording) "🎙️ 녹음 중지" else "🎙️ 녹음 시작")
                    }

                    Button(
                        onClick = {
                            if (selectedSentence != null && audioFile != null) {
                                viewModel.evaluatePronunciation(contentId, selectedSentence!!, audioFile!!)
                            }
                        },
                        enabled = selectedSentence != null && audioFile != null
                    ) {
                        Text("발음 평가 시작")
                    }
                }

                Spacer(Modifier.height(8.dp))

                result?.let {
                    Text("정확도: ${it.accuracy}")
                    Text("유창성: ${it.fluency}")
                    Text("완성도: ${it.completeness}")
                    Text("총점: ${it.pronunciation}")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text("선택된 문장", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = selectedSentence ?: "불러오는 중...",
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .background(Color(0xFFE3F2FD))
                        .padding(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("문장 바꾸기", style = MaterialTheme.typography.titleMedium)
            }

            val sentences = textDetail?.textFiles?.firstOrNull()?.sentences ?: emptyList()
            items(sentences.size) { index ->
                val s = sentences[index]
                Text(
                    text = s.originalText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setEvalSentence(s.originalText) }
                        .padding(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) } // 마지막 여백 확보
        }
    }
}


@Composable
fun RecordingIndicator(isRecording: Boolean) {
    if (isRecording) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            // 🔴
            val infiniteTransition = rememberInfiniteTransition()
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(500),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Red.copy(alpha = alpha), shape = CircleShape)
            )

            Text("녹음 중...", color = Color.Red, style = MaterialTheme.typography.bodyLarge)
        }
    }

     */
}
