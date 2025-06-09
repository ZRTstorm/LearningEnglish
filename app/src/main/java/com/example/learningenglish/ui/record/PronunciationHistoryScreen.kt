package com.example.learningenglish.ui.record

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationHistoryScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int
) {
    val history by viewModel.pronunciationHistory.collectAsState()
    var expandedId by remember { mutableStateOf<Int?>(null) }
    var expandedMetric by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadPronunciationHistory(userId, contentType, contentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("📝 발음 평가 기록") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("문장: ${item.sentenceId}", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                expandedId = if (expandedId == item.sentenceId) null else item.sentenceId
                                expandedMetric = null
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (expandedId == item.sentenceId) "닫기" else "자세히")
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = if (expandedId == item.sentenceId) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        if (expandedId == item.sentenceId) {
                            Spacer(modifier = Modifier.height(8.dp))

                            val metricKeys = listOf("정확도", "유창성", "완성도", "총점")
                            val scores = listOf(item.accuracyScore, item.fluencyScore, item.completenessScore, item.pronunciationScore)
                            val descriptions = listOf(
                                "발음이 정확했는가",
                                "말이 끊김 없이 자연스럽게 이어졌는가",
                                "단어를 모두 발음했는가",
                                "전체 종합 평가 점수"
                            )

                            val descriptionVisibleMap = remember { mutableStateMapOf<String, Boolean>() }

                            metricKeys.forEachIndexed { index, label ->
                                val score = scores[index]
                                val description = descriptions[index]
                                val isVisible = descriptionVisibleMap[label] ?: false

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(label, style = MaterialTheme.typography.labelMedium)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("(${score.toInt()}점)", style = MaterialTheme.typography.bodySmall)

                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("❕", modifier = Modifier.clickable {
                                                descriptionVisibleMap[label] = !isVisible
                                            })
                                        }
                                    }

                                    LinearProgressIndicator(
                                        progress = (score.toFloat() / 100f).coerceIn(0f, 1f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .padding(vertical = 4.dp),
                                        color = Color(0xFF6D9886)
                                    )

                                    if (isVisible) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }


                            /*
                            listOf(
                                Triple("정확도", item.accuracyScore, "발음이 정확했는가"),
                                Triple("유창성", item.fluencyScore, "말이 끊김 없이 자연스럽게 이어졌는가"),
                                Triple("완성도", item.completenessScore, "단어를 모두 발음했는가"),
                                Triple("총점", item.pronunciationScore, "전체 종합 평가 점수")
                            ).forEach { (label, score, description) ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                                        .clickable {
                                            expandedMetric = if (expandedMetric == label) null else label
                                        }
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(label, style = MaterialTheme.typography.titleSmall)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.HelpOutline,
                                            contentDescription = "도움말",
                                            tint = Color.Gray
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = (score.toFloat() / 100f).coerceIn(0f, 1f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .padding(vertical = 6.dp),
                                        color = Color(0xFF6D9886)
                                    )
                                    Text("${score.toInt()}점", style = MaterialTheme.typography.bodySmall)

                                    if (expandedMetric == label) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.White, RoundedCornerShape(6.dp))
                                                .padding(10.dp)
                                        ) {
                                            Text("! $description", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }*/

                            Text("💬 피드백:", style = MaterialTheme.typography.labelMedium)
                            item.feedback.raw.lines().take(3).forEach { msg ->
                                Text("- $msg", style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("평가 시각: ${item.evaluatedAt}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationHistoryScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int
) {
    val history by viewModel.pronunciationHistory.collectAsState()
    var expandedId by remember { mutableStateOf<Int?>(null) }


    LaunchedEffect(Unit) {
        viewModel.loadPronunciationHistory(userId, contentType, contentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("📝 발음 평가 기록") })
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(history) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("문장: ${item.sentenceId}", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = {
                                expandedId = if (expandedId == item.sentenceId) null else item.sentenceId
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(if (expandedId == item.sentenceId) "닫기" else "자세히")
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                imageVector = if (expandedId == item.sentenceId) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null
                            )
                        }

                        if (expandedId == item.sentenceId) {
                            Spacer(modifier = Modifier.height(8.dp))

                            listOf(
                                Triple("정확도", item.accuracyScore, "발음이 정확했는가"),
                                Triple("유창성", item.fluencyScore, "말이 끊김 없이 자연스럽게 이어졌는가"),
                                Triple("완성도", item.completenessScore, "단어를 모두 발음했는가"),
                                Triple("총점", item.pronunciationScore, "전체 종합 평가 점수")
                            ).forEach { (label, score, description) ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFF2F2F2), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text("$label", style = MaterialTheme.typography.labelMedium)
                                    LinearProgressIndicator(
                                        progress = (score.toFloat() / 100f).coerceIn(0f, 1f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .padding(vertical = 4.dp),
                                        color = Color(0xFF6D9886)
                                    )
                                    //
                                    //Text(description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text("💬 피드백:", style = MaterialTheme.typography.labelMedium)
                            item.feedback.raw.lines().take(3).forEach { msg ->
                                Text("- $msg", style = MaterialTheme.typography.bodySmall)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("평가 시각: ${item.evaluatedAt}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
*/
