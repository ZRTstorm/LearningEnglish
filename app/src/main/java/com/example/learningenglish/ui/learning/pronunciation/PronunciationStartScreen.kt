package com.example.learningenglish.ui.learning.pronunciation

import android.net.Uri
import android.widget.Toast
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
                    audioFile?.let { mp4File ->
                        val wavFile = File(context.cacheDir, "converted_audio.wav")
                        convertMp4ToWav(mp4File, wavFile) { success ->
                            if (success) {
                                viewModel.evaluatePronunciation(
                                    safeStartResult.sentenceId,
                                    safeStartResult.contentLibraryId,
                                    wavFile
                                )
                                navController.navigate("pronunciation/result/$contentType/$contentId")
                            } else {
                                Toast.makeText(context, "변환 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
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

 */
