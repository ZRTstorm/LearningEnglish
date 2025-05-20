package com.example.learningenglish.ui.learning

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.animation.core.Animatable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LearningStartScreen(
    navController: NavController,
    goalHours: Int,
    goalMinutes: Int,
    selectedLearningType: String
) {
    var elapsedSeconds by remember { mutableStateOf(0) }
    val characterX = remember { Animatable(0f) }
    val totalSeconds = (goalHours * 60 + goalMinutes) * 60

    val userUid = FirebaseAuth.getInstance().currentUser?.uid

    // 시간 흐르게 + 캐릭터 이동
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

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "학습 준비 완료!",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "목표 시간: ${goalHours}시간 ${goalMinutes}분",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "선택한 기능: $selectedLearningType",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {

                when (selectedLearningType) {
                    "자료 학습" -> navController.navigate("datalearningstart")
                    "받아쓰기" -> navController.navigate("dictation")
                    "발음 평가" -> navController.navigate("pronunciation")
                    "라이브러리" -> navController.navigate("library")
                    "단어장" -> {
                        if (userUid != null) {
                            navController.navigate("uservocab/$userUid")
                        }
                    }
                    else -> {
                        // 기본 fallback
                        navController.navigate("home")
                    }
                }
            }
        ) {
            Text("학습 시작하기")
        }
    }
}

