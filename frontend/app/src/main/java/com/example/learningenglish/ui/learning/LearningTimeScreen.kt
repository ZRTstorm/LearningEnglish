package com.example.learningenglish.ui.learning

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import com.example.learningenglish.R

@Composable
fun LearningProgressBar(
    navController: NavController,
    goalHours: Int,
    goalMinutes: Int,
    selectedLearningType: String
) {
    var elapsedSeconds by remember { mutableStateOf(0) }
    val characterX = remember { Animatable(0f) }
    val totalSeconds = (goalHours * 60 + goalMinutes) * 60
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${elapsedSeconds / 60}분 ${(elapsedSeconds % 60)}초 학습 중...",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_character), // 여기 캐릭터 이미지!
                contentDescription = "캐릭터",
                modifier = Modifier
                    .size(64.dp)
                    .offset(
                        x = (characterX.value * (screenWidth - 64.dp)).coerceAtMost(screenWidth - 64.dp)
                    )
            )
        }
    }
}
