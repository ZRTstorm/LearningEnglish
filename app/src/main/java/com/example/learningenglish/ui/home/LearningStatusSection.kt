package com.example.learningenglish.ui.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LearningStatusSection() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("이번주 학습", "다른 사람 학습량")

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEachIndexed { index, title ->
                Text(
                    text = title,
                    modifier = Modifier
                        .clickable { selectedTab = index }
                        .padding(8.dp),
                    style = if (selectedTab == index) {
                        MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    } else {
                        MaterialTheme.typography.bodyMedium
                    }
                )
            }
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp) // ✅ 탭과 그래프 사이 구분선 추가
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> WeeklyLearningGraph()
            1 -> OtherUsersLearningGraph()
        }
    }
}

@Composable
fun AnimatedBar(heightValue: Int, color: Color) {
    val animatedHeight by animateDpAsState(
        targetValue = (heightValue * 1.5).dp,
        animationSpec = tween(durationMillis = 800),
        label = "bar_animation"
    )

    Box(
        modifier = Modifier
            .width(20.dp)
            .height(animatedHeight)
            .background(color, shape = RoundedCornerShape(4.dp))
    )
}

@Composable
fun WeeklyLearningGraph() {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    val data = listOf(50, 60, 45, 70, 0, 0, 0)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                AnimatedBar(heightValue = value, color = Color(0xFF4CAF50))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = days[index], style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun OtherUsersLearningGraph() {
    val people = listOf("A", "B", "나", "C", "D", "E", "F")
    val data = listOf(20, 30, 40, 50, 60, 70, 90)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { index, value ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                AnimatedBar(heightValue = value, color = Color(0xFF2196F3))
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = people[index], style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}


