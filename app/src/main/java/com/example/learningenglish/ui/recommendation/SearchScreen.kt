package com.example.learningenglish.ui.recommendation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    userId: Int,
    viewModel: LearningViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var queryText by remember { mutableStateOf("") }
    var difficultyRange by remember { mutableStateOf(1f..15f) }
    var searchResults by remember { mutableStateOf<List<Pair<String, Int>>>(emptyList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("콘텐츠 검색") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                label = { Text("주제 텍스트 입력") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("난이도 범위: ${difficultyRange.start.toInt()} ~ ${difficultyRange.endInclusive.toInt()}")
            RangeSlider(
                value = difficultyRange,
                onValueChange = { difficultyRange = it },
                valueRange = 1f..15f
            )

            Text(
                text = "📘 도움말: 1~12는 초중고 수준, 13~15는 대학생 이상 전문 수준입니다.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        val results = viewModel.searchContentByText(
                            userId,
                            queryText,
                            difficultyRange.start,
                            difficultyRange.endInclusive
                        )
                        searchResults = results
                    }
                }
            ) {
                Text("검색")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(searchResults) { result ->
                    val contentType = result.first
                    val contentId = result.second

                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("Type: $contentType / ID: $contentId")
                        Button(onClick = {
                            coroutineScope.launch {
                                viewModel.addContentToLibrary(contentType, contentId, userId)
                            }
                        }) {
                            Text("라이브러리에 추가")
                        }
                    }
                }
            }
        }
    }
}
