package com.example.learningenglish.ui.learning.DataLearning.upload

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.repository.WordRepository
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import com.example.learningenglish.viewmodel.LearningViewModelFactory
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrResultScreen(navController: NavController, userId: String?, extractedTitle: String, extractedText: String) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    val coroutineScope = rememberCoroutineScope()
    val learningRepository = LearningRepository()
    val wordRepository = WordRepository(context, userPrefs)

    val viewModel: LearningViewModel = viewModel(factory = LearningViewModelFactory(
            repository = learningRepository,
            repositoryW = wordRepository
        )
    )
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("OCR 결과") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("제목: $extractedTitle", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("텍스트:\n$extractedText")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        //val uid = userPrefs.getUserId().firstOrNull()
                        if (userId != null) {
                            isSubmitting = true
                            viewModel.submitOcrText(
                                text = extractedText,
                                title = extractedTitle,
                                userId = userId,
                                onSuccess = {
                                    Toast.makeText(context, "OCR 등록 성공!", Toast.LENGTH_SHORT).show()
                                    navController.navigate("library")
                                },
                                onError = {
                                    Toast.makeText(context, "등록 실패: $it", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "로그인 정보 없음", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isSubmitting
            ) {
                Text(if (isSubmitting) "등록 중..." else "서버에 등록")
            }
        }
    }
}

