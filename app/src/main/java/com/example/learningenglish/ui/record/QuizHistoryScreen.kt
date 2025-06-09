package com.example.learningenglish.ui.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizHistoryScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int,
    latestQuizId: Int? = null
) {
    val quizHistory by viewModel.quizHistory.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadQuizHistory(userId, contentType, contentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🧠 퀴즈 풀기 기록") })
        }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            if (quizHistory.isEmpty()) {
                Text("기록이 없습니다.", style = MaterialTheme.typography.bodyLarge)
            } else {
                quizHistory.forEach { item ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        if (latestQuizId != null && item.id == latestQuizId) {
                            Text("⭐ <최근 기록>", style = MaterialTheme.typography.bodyMedium)
                        }
                        Text("📝 퀴즈 종류: ${item.quizType}")
                        Text("📊 점수: ${item.score}")
                        Text("🕒 날짜: ${item.date}")
                        Text("📍 원본 데이터: ${item.originalData}")
                        Text("🙋 사용자 응답: ${item.userData}")
                    }
                }
            }
        }
    }
}
