package com.example.learningenglish.ui.learning.pronunciation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.data.model.ImportantSentence
import com.example.learningenglish.viewmodel.LearningViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryPronunciationScreen(
    navController: NavController,
    viewModel: LearningViewModel,
    contentsType: String,
    contentId: Int
) {
    /*
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val evalResult by viewModel.evalResult.collectAsState()
    val summaryText by viewModel.summaryText.collectAsState()
    val importantList by viewModel.importantSentences.collectAsState()
    var audioFile by remember { mutableStateOf<File?>(null) }
    var isRecording by remember { mutableStateOf(false) }

    val audioRecorder = remember(context) { AudioRecorder() }


    LaunchedEffect(contentId) {
        viewModel.loadSummary(contentId)
        viewModel.loadImportantSentences(contentId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("📘 요약/핵심 발음 평가") }) },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        if (!isRecording) {
                            audioFile = audioRecorder.startRecording(context)
                            isRecording = true
                        } else {
                            val result = audioRecorder.stopRecording()
                            if (result != null) {
                                audioFile = result
                                val targetText = summaryText ?: importantList.joinToString(" ") { it.originalText }
                                if (targetText.isNotBlank()) {
                                    viewModel.evaluatePronunciation(contentId, targetText, result)
                                }
                            }
                            isRecording = false
                        }
                    }) {
                        Text(if (isRecording) "🎙️ 녹음 중지" else "🎙️ 녹음 시작")
                    }

                    Button(
                        onClick = {
                            val targetText = summaryText ?: importantList.joinToString(" ") { it.originalText }
                            if (targetText.isNotBlank() && audioFile != null) {
                                viewModel.evaluatePronunciation(contentId, targetText, audioFile!!)
                            }
                        },
                        enabled = audioFile != null
                    ) {
                        Text("발음 평가")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                evalResult?.let {
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
                .padding(16.dp)
        ) {
            item {
                Text("요약문장", style = MaterialTheme.typography.titleMedium)
                Text(summaryText ?: "로딩 중...")

                Spacer(modifier = Modifier.height(16.dp))

                Text("핵심 문장", style = MaterialTheme.typography.titleMedium)
            }

            items(importantList) { sentence ->
                Text(
                    text = sentence.originalText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setEvalSentence(sentence.originalText)
                        }
                        .padding(12.dp)
                )
            }
        }
    }

     */
}
