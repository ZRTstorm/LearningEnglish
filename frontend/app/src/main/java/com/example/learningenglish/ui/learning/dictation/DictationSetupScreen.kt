package com.example.learningenglish.ui.learning.dictation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.data.model.DictationStartRequest
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictationSentenceTypeSelectScreen(
    navController: NavController,
    contentId: Int,
    contentsType: String,
    viewModel: LearningViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var sentenceType by remember { mutableStateOf("important") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val userPrefs = UserPreferencesDataStore(context)
    var userId by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().first() ?: 0
    }
    var sentenceLevel by remember { mutableIntStateOf(10) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("문장 난이도 선택") })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp),
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
                        //val userId =
                        val request = DictationStartRequest(userId,contentId, contentsType, sentenceLevel)
                        val response = viewModel.startDictation(request)
                        if (response != null) {
                            navController.navigate("dictation/$contentId/$contentsType/$sentenceLevel")
                        } else {
                            // 에러 처리
                        }
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("받아쓰기 시작")
            }
        }
    }
}


@Composable
fun DropdownMenuType(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onSelect(option)
                    expanded = false
                }, text = { Text(option) })
            }
        }
    }
}