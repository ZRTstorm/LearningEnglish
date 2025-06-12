package com.example.learningenglish.ui.learning.pronunciation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import kotlin.math.abs


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
    val scope = rememberCoroutineScope()
    val mediaPlayer = remember { MediaPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    var isRecording by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    var elapsedTimeMs by remember { mutableStateOf(0L) }
    var showRecorder by remember { mutableStateOf(false) }
    val amplitudeList = remember { mutableStateListOf<Float>() }

    LaunchedEffect(isRecording && !isPaused) {
        while (isRecording && !isPaused) {
            delay(100)
            elapsedTimeMs += 100
            val amp = audioRecorder.currentAmplitude()
            amplitudeList.add(amp)
            if (amplitudeList.size > 60) {
                amplitudeList.removeAt(0)
            }
        }
    }

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val seconds = totalSeconds % 60
        val milliseconds = (ms % 1000) / 10
        return String.format("%02d:%02d", seconds, milliseconds)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}.mp3")
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
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🎤 발음 평가", style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("library") }) {

                        Icon(Icons.Default.Close, contentDescription = "닫기")
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
            verticalArrangement = Arrangement.Top
        ) {
            if (startResult == null) {
                CircularProgressIndicator()
                return@Column
            }
            val safeStartResult = startResult ?: return@Column

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("문장", style = MaterialTheme.typography.titleMedium)
                val levelColor = when (safeStartResult.level.toInt()) {
                    in 1..20 -> Color(0xFFBBDEFB)
                    in 21..40 -> Color(0xFFC8E6C9)
                    in 41..60 -> Color(0xFFFFF9C4)
                    in 61..80 -> Color(0xFFFFE0B2)
                    else -> Color(0xFFFFCDD2)
                }
                Box(
                    modifier = Modifier
                        .background(levelColor, shape = CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Lv.${safeStartResult.level.toInt()}", fontSize = 12.sp)
                }
            }

// 문장 표시
            Text(
                text = safeStartResult.sentence,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFECEFF1), shape = RoundedCornerShape(8.dp))
                    .padding(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            var showUploadHelp by remember { mutableStateOf(false) }
            var showRecordHelp by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("!", modifier = Modifier.clickable { showUploadHelp = !showUploadHelp }, fontSize = 12.sp)
                Spacer(Modifier.width(80.dp))
                Text("!", modifier = Modifier.clickable { showRecordHelp = !showRecordHelp }, fontSize = 12.sp)
            }

            if (showUploadHelp || showRecordHelp) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text(
                        text = if (showUploadHelp) "평가에 사용할 음성 파일을 업로드하세요" else "",
                        fontSize = 10.sp
                    )
                    Text(
                        text = if (showRecordHelp) "평가에 사용할 음성을 직접 녹음하세요" else "",
                        fontSize = 10.sp
                    )
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { launcher.launch("audio/*") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text("파일 업로드", color = Color.White) }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        showRecorder = true
                        isRecording = true
                        elapsedTimeMs = 0L
                        amplitudeList.clear()
                        audioFile = audioRecorder.startRecording(context)
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) { Text("녹음", color = Color.White) }
            }

            if (showRecorder) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Black, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.Center),horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isRecording) {
                            Text("녹음중... ${formatTime(elapsedTimeMs)}", color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            VoiceWaveform(amplitudeList.toList())
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = {
                                val file = audioRecorder.stopRecording()
                                if (file != null && file.exists()) {
                                    audioFile = file
                                    isRecording = false
                                } else {
                                    Toast.makeText(context, "녹음 파일 생성 실패", Toast.LENGTH_SHORT).show()
                                }
                            }) {
                                Text("■")
                            }
                        } else {
                            Text("녹음 완료!", color = Color.White)
                            Spacer(modifier = Modifier.height(8.dp))
                            VoiceWaveform(amplitudeList.toList())
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                Button(onClick = {
                                    audioFile?.let { file ->
                                        if (file.exists()) {
                                            try {
                                                mediaPlayer.reset()
                                                mediaPlayer.setDataSource(context, file.toUri())
                                                mediaPlayer.prepare()
                                                mediaPlayer.start()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                                Toast.makeText(context, "녹음 재생 실패", Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            Toast.makeText(context, "녹음 파일이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }) {
                                    Text("▶")
                                }
                                Button(onClick = {
                                    showRecorder = false
                                    elapsedTimeMs = 0L
                                    audioFile = null
                                }) {
                                    Text("⟳")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("${formatTime(elapsedTimeMs)}", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    audioFile?.let { mp4File ->
                        val outputFileName = "converted_${System.currentTimeMillis()}.wav"
                        val wavFile = File(context.cacheDir, outputFileName)
                        convertMp4ToWav(mp4File, wavFile) { success ->
                            if (success && wavFile.exists()) {
                                scope.launch {
                                    Toast.makeText(context, "📤 파일 업로드 완료! 평가 중입니다...", Toast.LENGTH_SHORT).show()
                                    viewModel.evaluatePronunciation(
                                        safeStartResult.sentenceId,
                                        safeStartResult.contentLibraryId,
                                        wavFile
                                    )
                                    navController.navigate("pronunciation/result/$contentType/$contentId")
                                }
                            } else {
                                Toast.makeText(context, "음성 변환 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                enabled = audioFile != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("발음 평가")
            }
        }
    }
}
