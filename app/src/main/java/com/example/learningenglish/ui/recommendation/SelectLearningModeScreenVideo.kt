package com.example.learningenglish.ui.recommendation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLearningModeVideoScreen(
    navController: NavController,
    contentId: Int,
    contentsType: String // text 또는 video
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    var userId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().firstOrNull()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("학습 모드 선택") })
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            Text("학습할 모드를 선택하세요", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    navController.navigate("videodetail/video/${contentId}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(" 학습하기 ")
            }

            Button(
                onClick = {
                    navController.navigate("pronunciation_sentence_type/$contentsType/$contentId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🎤 발음 평가")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    navController.navigate("pronunciation_history/$userId/video/$contentId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📝 발음 평가 기록")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    navController.navigate("dictation_sentence_type/$contentsType/$contentId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("✍ 받아쓰기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    navController.navigate("dictation_history/$userId/video/$contentId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📝 받아쓰기 기록")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    navController.navigate("quiz/$userId/video/$contentId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🧠 퀴즈 풀기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val latestQuizId = -1 // 퀴즈 안 푼 경우에도 무조건 -1로 전달
                    navController.navigate("quiz_history/$userId/video/$contentId?latestQuizId=$latestQuizId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📝 퀴즈 기록")
            }
        }
    }
}
