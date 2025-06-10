package com.example.learningenglish.ui.quiz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsertionResultScreen(
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int,
    quizId: Int,
    viewModel: LearningViewModel
) {
    var sentenceList by remember { mutableStateOf(listOf<String>()) }
    var originalNumList by remember { mutableStateOf(listOf<Int>()) }
    var userNumList by remember { mutableStateOf(listOf<Int>()) }
    var isLoaded by remember { mutableStateOf(false) }

    val score by remember(originalNumList, userNumList) {
        derivedStateOf {
            if (originalNumList.isNotEmpty() && userNumList.size == originalNumList.size) {
                val correct = originalNumList.zip(userNumList).count { it.first == it.second }
                (correct * 100) / originalNumList.size
            } else 0
        }
    }

    LaunchedEffect(quizId) {
        try {
            val feedback = viewModel.loadInsertionFeedback(quizId)
            sentenceList = feedback.sentenceList
            originalNumList = feedback.originalNumList
            userNumList = feedback.userNumList
            isLoaded = true
        } catch (_: Exception) {}
    }

    if (!isLoaded) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val originalSentences = originalNumList.map { sentenceList[it] }
    val userSentences = userNumList.map { sentenceList[it] }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("삽입퀴즈결과") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), contentAlignment = Alignment.CenterEnd) {
                Text("🎯 $score/100", style = MaterialTheme.typography.titleMedium, color = Color.Red)
            }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(originalSentences.size) { i ->
                    val isCorrect = originalSentences[i] == userSentences[i]
                    val indicator = if (isCorrect) "⭕" else "❌"
                    val bgColor = if (isCorrect) Color(0xFFE0F7FA) else Color(0xFFFFEBEE)

                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = bgColor)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Text(
                                text = indicator,
                                modifier = Modifier.padding(end = 12.dp),
                                color = Color.Red
                            )
                            Column {
                                Text("정답: ${originalSentences[i]}", style = MaterialTheme.typography.bodyMedium)
                                Spacer(Modifier.height(4.dp))
                                Text("내 답: ${userSentences[i]}", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        navController.navigate("quiz_history/$userId/$contentType/$contentId?latestQuizId=$quizId")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("복습하기")
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = {
                        navController.navigate("insertion_quiz/$userId/$contentType/$contentId")
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("다음 문제로")
                }
            }
        }
    }
}

