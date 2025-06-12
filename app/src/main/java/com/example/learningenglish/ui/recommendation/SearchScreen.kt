package com.example.learningenglish.ui.recommendation

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.data.model.AudioData
import com.example.learningenglish.data.model.SummaContentResponse
import com.example.learningenglish.data.remote.RetrofitInstance
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
    val snackbarHostState = remember { SnackbarHostState() }

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
                items(searchResults) { (contentType, contentId) ->
                    SearchResultCard(
                        contentType = contentType,
                        contentId = contentId,
                        userId = userId,
                        viewModel = viewModel,
                        navController = navController,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(
    contentType: String,
    contentId: Int,
    userId: Int,
    viewModel: LearningViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    var summa by remember { mutableStateOf<SummaContentResponse?>(null) }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var inputTitle by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(contentType, contentId) {
        summa = viewModel.fetchSummaContent(contentType, contentId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)) {
            if (summa != null) {
                Text("🎬 제목: ${summa!!.title}", style = MaterialTheme.typography.titleMedium)
                Text("📊 텍스트 점수: ${"%.1f".format(summa!!.textGrade)} / 사운드 점수: ${"%.1f".format(summa!!.soundGrade)}")
                Text("🔗 링크: ${summa!!.videoUrl}", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("정보 불러오는 중...", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(onClick = {
                showDialog = true
                /*
                coroutineScope.launch {
                    viewModel.addContentToLibrary(contentType, contentId, userId)
                    Toast.makeText(context, "라이브러리에 추가되었습니다.", Toast.LENGTH_SHORT).show()
                }

             */
            }) {
                Text("라이브러리에 추가")
            }
        }

        if (showDialog && summa != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("제목 입력") },
                text = {
                    OutlinedTextField(
                        value = inputTitle,
                        onValueChange = { inputTitle = it },
                        label = { Text("저장할 제목") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        Toast.makeText(context, "등록 중입니다..", Toast.LENGTH_SHORT).show()

                        coroutineScope.launch {
                            try {
                                val audioData = AudioData(
                                    userId = userId,
                                    title = inputTitle.text,
                                    url = summa!!.videoUrl
                                )
                                val response = RetrofitInstance.api.uploadAudio(audioData)
                                if (response.isSuccessful) {
                                    withContext(Dispatchers.Main) {
                                        val result = snackbarHostState.showSnackbar(
                                            message = "라이브러리에 등록 완료!",
                                            actionLabel = "이동",
                                            duration = SnackbarDuration.Short
                                        )
                                        if (result == SnackbarResult.ActionPerformed) {
                                            navController.navigate("library")
                                        }
                                    }
                                } else {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "등록 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "오류 발생: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }) {
                        Text("등록")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("취소")
                    }
                }
            )
        }
    }
}


