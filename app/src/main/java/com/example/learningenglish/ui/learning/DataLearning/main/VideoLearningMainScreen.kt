package com.example.learningenglish.ui.learning.DataLearning.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.learningenglish.R
import com.example.learningenglish.data.model.AudioContent
import com.example.learningenglish.data.model.LearningResponse
import com.example.learningenglish.data.model.UploadResponse
import com.example.learningenglish.data.remote.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLearningMainScreen(
    learningResponse: LearningResponse,
    navController: NavController,
    goalHours: Int,
    goalMinutes: Int
) {
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var showOriginalOnly by remember { mutableStateOf(true) }
    var showExitDialog by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf<String?>(null) }
    var favoriteWords = remember { mutableStateListOf<String>() }

    // 화면 크기와 애니메이션 관련 변수 설정
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var elapsedSeconds by remember { mutableStateOf(0) }
    val characterX = remember { Animatable(0f) }
    val totalSeconds = (goalHours * 60 + goalMinutes) * 60

    val timings = learningResponse.timings ?: emptyList()
    var currentTime by remember { mutableStateOf(0L) }
    val totalDuration = timings.lastOrNull()?.endTimeMillis ?: 0L
    var selectedSentenceIndex by remember { mutableStateOf(-1) }

    val progress by remember {
        derivedStateOf {
            if (totalDuration == 0L) 0f else currentTime.toFloat() / totalDuration
        }
    }

    // audioContent 상태를 관리
    var audioContent by remember { mutableStateOf<AudioContent?>(null) }

    LaunchedEffect(Unit) {
        while (elapsedSeconds < totalSeconds) {
            delay(1000)
            elapsedSeconds++
            characterX.animateTo(
                targetValue = (elapsedSeconds.toFloat() / totalSeconds),
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    // Retrofit을 통해 audio content를 가져오는 함수
    fun fetchAudioContent(audioId: String) {
        // Retrofit을 사용하여 GET 요청을 보내는 코드
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Retrofit을 통해 API 호출
                val response = RetrofitInstance.api.getAudio(audioId)  // 여기서 audioId를 사용

                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받았다면, 데이터 처리
                    val fetchedAudioContent = response.body()

                    // 화면 업데이트
                    withContext(Dispatchers.Main) {
                        // fetchedAudioContent는 서버에서 반환된 `AudioContent` 객체입니다.
                        audioContent = fetchedAudioContent  // 상태 변경
                        val contentId = fetchedAudioContent?.contentId  // 서버에서 반환된 contentId
                        println("Received contentId: $contentId")

                        // `contentId`를 활용하여 화면을 업데이트하거나 다른 처리할 수 있습니다.

                    }
                } else {
                    // 실패 처리 (예: 오류 메시지 출력)
                    withContext(Dispatchers.Main) {
                        println("Failed to get audio content: ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                // 예외 처리
                withContext(Dispatchers.Main) {
                    println("Exception occurred: ${e.localizedMessage}")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("영상 학습") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home")
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 원문/번역 보기 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showOriginalOnly = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("원문만")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showOriginalOnly = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("번역과 동시에")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 속도 조절 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SpeedButton(speed = 0.5f, currentSpeed = playbackSpeed) { playbackSpeed = it }
                SpeedButton(speed = 1.0f, currentSpeed = playbackSpeed) { playbackSpeed = it }
                SpeedButton(speed = 2.0f, currentSpeed = playbackSpeed) { playbackSpeed = it }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 원문/번역 텍스트 표시
            if (showOriginalOnly) {
                Text(learningResponse.originalText)
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = learningResponse.originalText,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = learningResponse.translatedText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 진행 상태바
            Slider(
                value = progress,
                onValueChange = { newProgress ->
                    val newTime = (newProgress * totalDuration).toLong()
                    currentTime = newTime
                    fetchAudioContent(learningResponse.contentId)  // 슬라이더 조정 시 API 호출
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text("${(progress * 100).toInt()}% 진행 중", modifier = Modifier.align(Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(16.dp))

            // 원문/번역 세그먼트 표시
            if (showOriginalOnly) {
                Column {
                    timings.forEachIndexed { index, timing ->
                        Text(
                            text = timing.segmentOriginalText,
                            modifier = Modifier
                                .clickable {
                                    currentTime = timing.startTimeMillis
                                    selectedSentenceIndex = index
                                    fetchAudioContent(learningResponse.contentId)  // 구문 클릭 시 API 호출
                                }
                                .padding(8.dp)
                        )
                    }
                }
            } else {
                Column {
                    timings.forEachIndexed { index, timing ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = timing.segmentOriginalText,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        currentTime = timing.startTimeMillis
                                        selectedSentenceIndex = index
                                        fetchAudioContent(learningResponse.contentId)  // 구문 클릭 시 API 호출
                                    }
                                    .padding(8.dp)
                            )
                            Text(
                                text = timing.segmentTranslatedText,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 캐릭터 이동
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_character),
                    contentDescription = "캐릭터",
                    modifier = Modifier
                        .size(64.dp)
                        .offset(
                            x = (characterX.value * (screenWidth - 64.dp)).coerceAtMost(screenWidth - 64.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(elapsedSeconds * 100) / totalSeconds}% 진행 중...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SpeedButton(speed: Float, currentSpeed: Float, onClick: (Float) -> Unit) {
    Button(
        onClick = { onClick(speed) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (speed == currentSpeed) MaterialTheme.colorScheme.primary else Color.LightGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("${speed}x")
    }
}


