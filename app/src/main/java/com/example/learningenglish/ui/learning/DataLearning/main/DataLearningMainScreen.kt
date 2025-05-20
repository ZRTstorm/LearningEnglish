package com.example.learningenglish.ui.learning.DataLearning.main

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.learningenglish.R
import com.example.learningenglish.data.model.LearningResponse
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataLearningMainScreen(
    learningResponse: LearningResponse,
    navController: NavController,
    goalHours: Int,
    goalMinutes: Int,
) {
    var showOriginalOnly by remember { mutableStateOf(true) }
    var showExitDialog by remember { mutableStateOf(false) }

    var selectedWord by remember { mutableStateOf<String?>(null) }
    var favoriteWords = remember { mutableStateListOf<String>() }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    var elapsedSeconds by remember { mutableStateOf(0) }
    val characterX = remember { Animatable(0f) }
    val totalSeconds = (goalHours * 60 + goalMinutes) * 60

    LaunchedEffect(Unit) {
        while (elapsedSeconds < totalSeconds) {
            delay(1000)
            elapsedSeconds++
            characterX.animateTo(
                targetValue = (elapsedSeconds.toFloat() / totalSeconds),
                animationSpec = tween(durationMillis = 1000)
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("자료 학습") },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { showOriginalOnly = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("원문만")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { showOriginalOnly = false },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("번역과 동시에")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (showOriginalOnly) {
                WordClickableText(
                    text = learningResponse.originalText,
                    onWordClick = { word -> selectedWord = word }
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    WordClickableText(
                        text = learningResponse.originalText,
                        onWordClick = { word -> selectedWord = word },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    WordClickableText(
                        text = learningResponse.translatedText,
                        onWordClick = { /* 번역문 클릭은 무시 */ },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 캐릭터 이동
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_character),
                    contentDescription = "캐릭터",
                    modifier = Modifier
                        .size(64.dp)
                        .offset(
                            x = (characterX.value * (screenWidth - 64.dp)).coerceAtMost(screenWidth - 64.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${(elapsedSeconds * 100) / totalSeconds}% 진행 중...",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // 종료/다른자료 선택 다이얼로그
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("학습을 종료하시겠습니까?") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }) { Text("학습 종료") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    navController.navigate("datalearningstart")
                }) { Text("다른 자료로 학습") }
            }
        )
    }

    // 단어 상세 팝업
    selectedWord?.let { word ->
        Dialog(onDismissRequest = { selectedWord = null }) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(text = "단어: $word", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "뜻: (예시 뜻)", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "예문: (예시 문장)", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            favoriteWords.add(word)
                            selectedWord = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("⭐ 단어장에 추가하기")
                    }
                }
            }
        }
    }
}

@Composable
fun WordClickableText(
    text: String,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val words = text.split(" ")
    Row(modifier = modifier.wrapContentHeight()) {
        words.forEach { word ->
            Text(
                text = "$word ",
                modifier = Modifier
                    .clickable { onWordClick(word.trim()) }
                    .padding(2.dp)
            )
        }
    }
}
