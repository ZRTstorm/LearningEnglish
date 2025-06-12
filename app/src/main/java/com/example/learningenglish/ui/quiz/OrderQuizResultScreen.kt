package com.example.learningenglish.ui.quiz


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderQuizResultScreen(
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int,
    quizId: Int,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var score by remember { mutableIntStateOf(0) }
    var feedbackOriginal by remember { mutableStateOf(listOf<String>()) }
    var feedbackUser by remember { mutableStateOf(listOf<String>()) }
    var isLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        try {
            val order = viewModel.loadOrderFeedback(quizId)
            feedbackOriginal = order.originalText.map { it.text }
            feedbackUser = order.userOrders.mapNotNull { idx ->
                order.originalText.firstOrNull { s -> s.index == idx }?.text
            }
            score = viewModel.calculateOrderScore()
            isLoaded = true
        } catch (e: Exception) {
            Toast.makeText(context, "피드백 로딩 실패", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("배열 퀴즈 결과") },
                actions = {
                    IconButton(onClick = { navController.navigate("library") }) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
            )
        }
    ) { padding ->
        if (!isLoaded) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("문제 유형: 순서 배열", style = MaterialTheme.typography.headlineSmall)
                    Text("🎯 $score/100", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(feedbackOriginal.size) { i ->
                        val isCorrect = feedbackOriginal[i] == feedbackUser.getOrNull(i)
                        val bgColor = if (isCorrect) Color(0xFFE0F7FA) else Color(0xFFFFEBEE)
                        val indicator = if (isCorrect) "⭕" else "❌"


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = bgColor
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = indicator,
                                    color = Color.Red,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                                Column {
                                    Text("정답: ${feedbackOriginal[i]}", style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(4.dp))
                                    Text("내 답: ${feedbackUser.getOrNull(i) ?: "미응답"}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            navController.navigate("quiz_history/$userId/$contentType/$contentId?latestQuizId=$quizId")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("복습하기")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            navController.navigate("summaOrders_quiz/$userId/$contentType/$contentId")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("다음 문제로")
                    }
                }
            }
        }
    }
}


