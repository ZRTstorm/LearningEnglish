package com.example.learningenglish.ui.learning.DataLearning

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataLearningStartScreen(
    navController: NavController,
    onRegisterNewClick: () -> Unit,
    onUseExistingClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("자료 학습 시작") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        },
        containerColor = Color(0xFFFFFAF0)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("학습을 어떻게 시작할까요?", style = MaterialTheme.typography.headlineSmall)

            Button(
                onClick = onRegisterNewClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC))
            ) {
                Text("📥 새로 등록해서 학습하기", style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = onUseExistingClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0BEC5))
            ) {
                Text("📂 기존 콘텐츠로 학습하기", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
