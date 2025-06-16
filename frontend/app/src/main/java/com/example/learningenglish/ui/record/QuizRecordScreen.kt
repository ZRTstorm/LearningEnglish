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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch

@Composable
fun QuizRecordScreen(
    navController: NavController,
    viewModel: LearningViewModel,
    userId: Int
) {
    /*
    val context = LocalContext.current
    val groupedQuizHistory by viewModel.quizHistoryByLibrary

    LaunchedEffect(Unit) {
        viewModel.fetchQuizHistoryList(userId)
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        groupedQuizHistory.forEach { (libraryId, records) ->
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text("콘텐츠 ID: $libraryId", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        navController.navigate("quizDetail/$libraryId")
                    }) {
                        Text("기록 보기 (${records.size}회)")
                    }
                }
            }
        }
    }

     */
}
