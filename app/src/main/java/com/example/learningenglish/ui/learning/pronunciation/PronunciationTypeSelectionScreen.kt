package com.example.learningenglish.ui.learning.pronunciation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel

//안쓰는코드
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationTypeSelectionScreen(
    navController: NavController,
    contentId: Int,
    contentsType: String,
    viewModel: LearningViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("🎙️ 발음 평가 방식 선택") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "원하는 평가 방식을 선택하세요",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            PronunciationOptionCard(
                title = "📝 원하는 문장 평가",
                description = "본문의 원하는 문장을 선택해서 녹음하고 평가 받을 수 있어요.",
                onClick = {
                    navController.navigate("pronunciation_eval/${contentsType}/${contentId}")
                }
            )

            PronunciationOptionCard(
                title = "📘 요약/핵심 문장 평가",
                description = "서버에서 받은 요약 및 핵심 문장을 기반으로 발음을 평가해요.",
                onClick = {
                    navController.navigate("pronunciation_summary/${contentsType}/${contentId}")
                }
            )
        }
    }
}

@Composable
fun PronunciationOptionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F4FF), shape = CircleShape)
            .clickable { onClick() }
            .padding(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Color(0xFF0D47A1))
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
