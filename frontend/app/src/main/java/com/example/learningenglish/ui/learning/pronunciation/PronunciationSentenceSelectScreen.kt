package com.example.learningenglish.ui.learning.pronunciation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// 레벨 선택 + 문장 불러오기
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PronunciationSentenceSelectScreen(
    navController: NavController,
    contentId: Int,
    contentsType: String,
    viewModel: LearningViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val userPrefs = UserPreferencesDataStore(context)
    var userId by remember { mutableStateOf(0) }
    var sentenceLevel by remember { mutableIntStateOf(10) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().first() ?: 0
    }

    Scaffold(topBar = { TopAppBar(title = { Text("문장 난이도 선택") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).padding(24.dp)
        ) {
            Text("문장 난이도를 선택하세요 (1~100)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = sentenceLevel.toFloat(),
                onValueChange = { sentenceLevel = it.toInt() },
                valueRange = 1f..100f,
                steps = 98,
                modifier = Modifier.fillMaxWidth()
            )
            Text("선택된 난이도: $sentenceLevel")
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        val result = viewModel.startPronunciation(userId, contentsType, contentId, sentenceLevel)
                        if (result != null) {
                            navController.navigate("pronunciation/record/$contentsType/$contentId/$sentenceLevel")
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("문장 불러오기")
            }
        }
    }
}
