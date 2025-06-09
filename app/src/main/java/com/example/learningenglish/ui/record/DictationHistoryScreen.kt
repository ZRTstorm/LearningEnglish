package com.example.learningenglish.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictationHistoryScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int
) {
    val history by viewModel.dictationHistory.collectAsState()
    var expandedCardId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadDictationHistory(userId, contentType, contentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("📝 받아쓰기 기록") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            expandedCardId = if (expandedCardId == item.sentenceId) null else item.sentenceId
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("✏️ 문장: ${item.sentence}", style = MaterialTheme.typography.titleMedium)

                        if (expandedCardId == item.sentenceId) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("내가 작성한 문장: ${item.userText}")

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("문법 점수", style = MaterialTheme.typography.titleSmall)
                            LinearProgressIndicator(
                                progress = (item.grammarScore.toFloat() / 100f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .padding(vertical = 4.dp),
                                color = Color(0xFF6D9886)
                            )
                            Text("${item.grammarScore.toInt()}점", style = MaterialTheme.typography.bodySmall)

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("유사도 점수", style = MaterialTheme.typography.titleSmall)
                            LinearProgressIndicator(
                                progress = (item.similarityScore.toFloat() / 100f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .padding(vertical = 4.dp),
                                color = Color(0xFF6D9886)
                            )
                            Text("${item.similarityScore.toInt()}점", style = MaterialTheme.typography.bodySmall)

                            if (item.feedback.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("💬 피드백:", style = MaterialTheme.typography.labelMedium)
                                item.feedback.lines().take(3).forEach { msg ->
                                    Text("- $msg", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("평가 시각: ${item.createdAt}", style = MaterialTheme.typography.labelSmall)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = if (expandedCardId == item.sentenceId) "닫기" else "자세히 보기",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF6D9886)
                            )
                        }
                    }
                }
            }
        }
    }
}