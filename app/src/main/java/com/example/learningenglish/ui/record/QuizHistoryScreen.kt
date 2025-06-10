package com.example.learningenglish.ui.record

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    var selectedQuizType by remember { mutableStateOf("전체") }
    var sortByScore by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.loadQuizHistory(userId, contentType, contentId)
    }

    val filtered = quizHistory.filter {
        selectedQuizType == "전체" || it.quizType == selectedQuizType
    }.let {
        if (sortByScore) it.sortedByDescending { it.score } else it.sortedByDescending { it.date }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📚 퀴즈 기록") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    TextButton(onClick = { sortByScore = !sortByScore }) {
                        Text(if (sortByScore) "최신순" else "점수순")
                    }
                    QuizTypeDropdown(selectedQuizType) { selectedQuizType = it }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("총 ${filtered.size}회 응시", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(8.dp))

            filtered.forEach { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (item.id == latestQuizId) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (item.id == latestQuizId) {
                                Text("⭐ 최근 기록", modifier = Modifier.padding(end = 8.dp), color = MaterialTheme.colorScheme.primary)
                            }
                            if (item.score < 60) {
                                Text("🔥 복습 추천", modifier = Modifier.padding(end = 8.dp), color = MaterialTheme.colorScheme.error)
                            }
                            Text("📝 ${item.quizType}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(" 점수: ${item.score}")
                        Text(" 날짜: ${item.date}")

                        Spacer(Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(onClick = {
                                navController.navigate("${item.quizType}_result/$userId/$contentType/$contentId/${item.id}")
                            }) {
                                Text("기록 자세히 보기")
                            }
                            Button(onClick = {
                                navController.navigate("${item.quizType}_quiz/$userId/$contentType/$contentId")
                            }) {
                                Text("재시험 보기")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizTypeDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("전체", "insertion", "summaOrders")

    Box {
        TextButton(onClick = { expanded = true }) {
            Text("${selected} ▼")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach {
                DropdownMenuItem(text = { Text(it) }, onClick = {
                    onSelected(it)
                    expanded = false
                })
            }
        }
    }
}

