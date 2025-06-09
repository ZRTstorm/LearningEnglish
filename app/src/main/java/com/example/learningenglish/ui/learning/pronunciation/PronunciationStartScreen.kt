package com.example.learningenglish.ui.learning.pronunciation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationRecordScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    contentId: Int,
    contentType: String,
    sentenceLevel: Int

) {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder() }
    val startResult by viewModel.startResult.collectAsState()



    var isRecording by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File(context.cacheDir, "upload.mp3")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            audioFile = tempFile
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎤 발음 평가") },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (startResult == null) {
                CircularProgressIndicator()
                return@Column
            }

            val safeStartResult = startResult

            if (safeStartResult == null) {
                CircularProgressIndicator()
                return@Column
            }

            Text("문장", style = MaterialTheme.typography.titleMedium)
            Text(
                text = safeStartResult.sentence,
                modifier = Modifier
                    .background(Color(0xFFE1F5FE))
                    .padding(12.dp)
            )
            Box(
                modifier = Modifier
                    .background(Color(0xFFBBDEFB), shape = CircleShape)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("Lv.${safeStartResult.level.toInt()}")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    if (!isRecording) {
                        audioFile = audioRecorder.startRecording(context)
                        isRecording = true
                    } else {
                        val file = audioRecorder.stopRecording()
                        if (file != null) audioFile = file
                        isRecording = false
                    }
                }) {
                    Text(if (isRecording) "🎙️ 녹음 중지" else "🎙️ 녹음 시작")
                }

                Button(onClick = {
                    launcher.launch("audio/*")
                }) {
                    Text("파일 업로드")
                }
            }

            Button(
                onClick = {
                    audioFile?.let {
                        viewModel.evaluatePronunciation(
                            safeStartResult.sentenceId,
                            safeStartResult.contentLibraryId,
                            it
                        )
                        navController.navigate("pronunciation/result/$contentType/$contentId")
                    }
                },
                enabled = audioFile != null
            ) {
                Text("발음 평가")
            }
        }
    }
}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationStartAndEvalScreen(
    viewModel: LearningViewModel,
    userId: Int,
    contentType: String,
    contentId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder() }
    val startResult by viewModel.startResult.collectAsState()
    val evalResult by viewModel.evalResult.collectAsState()

    var isRecording by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var sentenceLevel by remember { mutableStateOf(50f) }

    // 파일 선택 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File(context.cacheDir, "upload.mp3")
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            audioFile = tempFile
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🎤 발음 평가") },
                actions = {
                    startResult?.let {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .background(Color(0xFFBBDEFB), shape = CircleShape)
                                .padding(12.dp)
                        ) {
                            Text("Lv.${it.level.toInt()}")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 문장 불러오기
            Text("레벨 선택: ${sentenceLevel.toInt()}")
            Slider(
                value = sentenceLevel,
                onValueChange = { sentenceLevel = it },
                valueRange = 1f..100f
            )
            Button(onClick = {
                viewModel.startPronunciation(
                    userId = userId,
                    contentType = contentType,
                    contentId = contentId,
                    sentenceLevel = sentenceLevel.toInt()
                )
            }) {
                Text("문장 불러오기")
            }

            if (startResult == null) {
                CircularProgressIndicator()
                return@Column
            }

            // 문장 표시
            Text("문장", style = MaterialTheme.typography.titleMedium)
            Text(
                text = startResult!!.sentence,
                modifier = Modifier
                    .background(Color(0xFFE1F5FE))
                    .padding(12.dp)
            )

            // 녹음 or 파일 선택
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    if (!isRecording) {
                        audioFile = audioRecorder.startRecording(context)
                        isRecording = true
                    } else {
                        val file = audioRecorder.stopRecording()
                        if (file != null) audioFile = file
                        isRecording = false
                    }
                }) {
                    Text(if (isRecording) "🎙️ 녹음 중지" else "🎙️ 녹음 시작")
                }

                Button(onClick = {
                    launcher.launch("audio/*")
                }) {
                    Text("파일 업로드")
                }
            }

            // 발음 평가
            Button(
                onClick = {
                    audioFile?.let {
                        viewModel.evaluatePronunciation(
                            startResult!!.sentenceId,
                            startResult!!.contentLibraryId,
                            it
                        )
                    }
                },
                enabled = audioFile != null
            ) {
                Text("발음 평가")
            }

            // 결과 출력
            evalResult?.let {
                Text("정확도: ${it.accuracy}")
                Text("유창성: ${it.fluency}")
                Text("완성도: ${it.completeness}")
                Text("총점: ${it.pronunciation}")
                Spacer(modifier = Modifier.height(8.dp))
                it.feedbackMessages.forEach { msg -> Text("- $msg") }
            }
        }
    }
}

 */