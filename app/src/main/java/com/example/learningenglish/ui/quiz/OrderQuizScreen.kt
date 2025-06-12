package com.example.learningenglish.ui.quiz

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


// 배열 퀴즈 화면
@Composable
fun OrderQuizScreen(
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var originalOrder by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var shuffledOrder by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var selectedOrder by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var isQuizLoaded by remember { mutableStateOf(false) }

    val isOrderCompleted by remember(selectedOrder, originalOrder) {
        derivedStateOf { selectedOrder.size == originalOrder.size }
    }

    LaunchedEffect(true) {
        try {
            val quizData = viewModel.loadOrderQuiz(userId, contentType, contentId)
            originalOrder = quizData.sentenceList.map { it.index to it.text }
            shuffledOrder = originalOrder.shuffled()
            isQuizLoaded = true
        } catch (e: Exception) {
            Toast.makeText(context, "퀴즈 로딩 실패", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = {
                    scope.launch {
                        viewModel.userAnswers = selectedOrder.map { it.first }.toMutableList()
                        viewModel.insertNumList = originalOrder.map { it.first }.toMutableList()
                        val quizId = viewModel.saveOrderQuizResult(userId, contentType, contentId)
                        navController.navigate("order_result/$userId/$contentType/$contentId/$quizId")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                enabled = isOrderCompleted
            ) {
                Text("제출")
            }
        }
    ) { padding ->
        if (!isQuizLoaded) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            Text("문제 유형: 순서 배열", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(shuffledOrder) { pair ->
                    val isSelected = selectedOrder.contains(pair)

                    Button(
                        onClick = {
                            //if (!selectedOrder.contains(pair)) selectedOrder = selectedOrder + pair
                            selectedOrder = if (isSelected) {
                                selectedOrder.filterNot { it == pair } // 선택 해제
                            } else {
                                selectedOrder + pair // 선택 추가
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF6D9886) else Color(0xFFE0E0E0),
                            contentColor = Color.Black
                        )
                    ) {
                        Text(pair.second)
                    }
                }
                item {
                    Spacer(Modifier.height(8.dp))
                    Text("선택한 순서", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))

                    selectedOrder.forEachIndexed { i, pair ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
                        ) {
                            Text(
                                text = "${i + 1}. ${pair.second}",
                                modifier = Modifier.padding(12.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

