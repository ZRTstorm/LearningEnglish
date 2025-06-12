package com.example.learningenglish.ui.recommendation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import kotlinx.coroutines.flow.firstOrNull

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLearningModeScreen(
    navController: NavController,
    contentId: Int,
    contentsType: String // text 또는 video
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    var userId by remember { mutableStateOf<Int?>(null) }
    val baseGreen = Color(0xFF6D9886)
    val pressedGreen = Color(0xFF5B8776)

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().firstOrNull()
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("학습 모드 선택") },
            navigationIcon = {
                IconButton(onClick = { navController.navigate("library") }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "뒤로가기"
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "닫기"
                    )
                }
            }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("학습할 모드를 선택하세요", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            GreenActionButton(
                onClick = {
                    navController.navigate("textdetail/text/${contentId}")
                },
                text = " 학습하기 "
            )

            Spacer(modifier = Modifier.height(24.dp))

            GreenActionButton(
                onClick = {
                    navController.navigate("pronunciation_sentence_type/$contentsType/$contentId")
                },
                text ="🎤 발음 평가"
            )


            Spacer(modifier = Modifier.height(12.dp))

            GreenActionButton(
                onClick = {
                    navController.navigate("pronunciation_history/$userId/text/$contentId")
                },
                text ="📝 발음 평가 기록"
            )


            Spacer(modifier = Modifier.height(12.dp))

            GreenActionButton(
                onClick = {
                    navController.navigate("dictation_sentence_type/$contentsType/$contentId")
                },
                text = "✍ 받아쓰기"
            )


            Spacer(modifier = Modifier.height(12.dp))

            GreenActionButton(
                onClick = {
                    navController.navigate("dictation_history/$userId/text/$contentId")
                },
                text = "📝 받아쓰기 기록"
            )

            Spacer(modifier = Modifier.height(12.dp))

            GreenActionButton(
                onClick = {
                    navController.navigate("quiz_select/$userId/text/$contentId")
                },
                text = "🧠 퀴즈 풀기"
            )


            Spacer(modifier = Modifier.height(12.dp))

            GreenActionButton(
                onClick = {
                    val latestQuizId = -1 // 퀴즈 안 푼 경우에도 무조건 -1로 전달
                    navController.navigate("quiz_history/$userId/text/$contentId?latestQuizId=$latestQuizId")
                },
                text = "📝 퀴즈 기록"
            )


            /*
            Button(
                onClick = {
                    navController.navigate("textdetail/text/${contentId}")
                },
                modifier = Modifier.fillMaxWidth(),
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
                    navController.navigate("pronunciation_history/$userId/text/$contentId")
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
                    navController.navigate("dictation_history/$userId/text/$contentId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📝 받아쓰기 기록")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    navController.navigate("quiz_select/$userId/text/$contentId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🧠 퀴즈 풀기")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    val latestQuizId = -1 // 퀴즈 안 푼 경우에도 무조건 -1로 전달
                    navController.navigate("quiz_history/$userId/text/$contentId?latestQuizId=$latestQuizId")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📝 퀴즈 기록")
            }

             */
        }
    }
}

@Composable
fun GreenActionButton(
    onClick: () -> Unit,
    text: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Color(0xFF5B8776) else Color(0xFF6D9886),
        label = "buttonColor"
    )

    Button(
        onClick = onClick,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor,contentColor = Color.White),
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text)
    }
}

