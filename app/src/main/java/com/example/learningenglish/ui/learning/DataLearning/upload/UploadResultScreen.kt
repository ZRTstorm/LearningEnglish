package com.example.learningenglish.ui.learning.DataLearning.upload

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.learningenglish.data.model.ContentType
import com.example.learningenglish.data.model.LearningResponse
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.util.Resource
import com.example.learningenglish.ui.learning.DataLearning.main.DataLearningMainScreen
import com.example.learningenglish.ui.learning.DataLearning.main.VideoLearningMainScreen

@Composable
fun UploadResultScreen(
    navController: NavController,
    goalHours: Int,
    goalMinutes: Int,
    contentId: String
) {
    // 응답을 받을 상태 변수
    var learningResponse by remember { mutableStateOf<LearningResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    /*
    // API 호출 (LaunchedEffect로 비동기 처리)
    LaunchedEffect(contentId) {
        try {
            // LearningRepository를 사용하여 contentId에 해당하는 학습 콘텐츠 조회
            val repository = LearningRepository()
            val response = repository.fetchLearningContent(contentId)

            when (response) {
                is Resource.Success -> {
                    learningResponse = response.data
                    isLoading = false
                }
                is Resource.Error -> {
                    errorMessage = response.message
                    isLoading = false
                }
            }
        } catch (e: Exception) {
            errorMessage = e.localizedMessage
            isLoading = false
        }
    }
    */

    // 로딩 중 UI
    if (isLoading) {
        CircularProgressIndicator()  // 로딩 화면 표시
    }
    // 오류 UI
    else if (errorMessage != null) {
        Text(text = "Error: $errorMessage")
    }
    // 데이터가 성공적으로 로드된 후 UI
    else if (learningResponse != null) {
        val response = learningResponse!!

        // contentType에 따라 다른 화면을 보여줌
        when (response.contentType) {
            "TEXT" -> {
                DataLearningMainScreen(
                    learningResponse = response,
                    navController = navController,
                    //goalHours = goalHours,
                    //goalMinutes = goalMinutes
                )
            }
            "VIDEO" -> {
                VideoLearningMainScreen(
                    learningResponse = response,
                    navController = navController,
                    //goalHours = goalHours,
                    //goalMinutes = goalMinutes
                )
            }
        }
    }
}