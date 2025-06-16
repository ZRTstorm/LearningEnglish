package com.example.learningenglish.ui.record

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.learningenglish.viewmodel.LearningViewModel

@Composable
fun QuizRecordDetailScreen(
    libraryId: Int,
    userId: Int, // ✅ userId 추가
    viewModel: LearningViewModel
) {
    /*
    val context = LocalContext.current
    val quizList = viewModel.
    getHistoriesForLibrary(libraryId) // ViewModel에서 가져오기

    LaunchedEffect(Unit) {
        try {
            viewModel.fetchQuizHistoryList(userId)
        } catch (e: Exception) {
            Toast.makeText(context, "기록 불러오기 실패", Toast.LENGTH_SHORT).show()
            Log.e("QuizDetail", "에러: ${e.localizedMessage}")
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("콘텐츠 ID: $libraryId 학습 기록", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(quizList) { quiz ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Text("유형: ${quiz.quizType}")
                    Text("점수: ${quiz.score}")
                    Text("기록: ${quiz.userData}")
                    Text("정답: ${quiz.originalData}")
                    Text("날짜: ${quiz.date}")
                }
            }
        }
    }

     */
}
