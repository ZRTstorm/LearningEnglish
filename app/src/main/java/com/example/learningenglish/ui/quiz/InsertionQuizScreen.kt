package com.example.learningenglish.ui.quiz

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import kotlin.random.Random
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*

// 삽입 퀴즈 화면
@Composable
fun InsertionQuizScreen(
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var quizId by remember { mutableStateOf(-1) }
    var fullSentences by remember { mutableStateOf(listOf<String>()) }
    var insertIndices by remember { mutableStateOf(listOf<Int>()) }
    var insertionChoices by remember { mutableStateOf(listOf<Pair<Int, String>>()) }

    var userSelectedMap by remember { mutableStateOf(mutableMapOf<Int, Int>()) }
    var selectedChoices by remember { mutableStateOf(listOf<Int>()) }

    var selectedTarget by remember { mutableStateOf<Int?>(null) }
    var isChoiceExpanded by remember { mutableStateOf(false) }
    var isQuizLoaded by remember { mutableStateOf(false) }
    var currentChoiceIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(true) {
        try {
            val quizData = viewModel.loadQuiz(userId, contentType, contentId).also {
                quizId = it.quizId
            }
            fullSentences = quizData.sentenceList
            insertIndices = viewModel.insertNumList
            insertionChoices = insertIndices.map { index -> index to quizData.sentenceList[index] }.shuffled()
            isQuizLoaded = true
        } catch (e: Exception) {
            Toast.makeText(context, "퀴즈 로딩 실패", Toast.LENGTH_SHORT).show()
        }
    }

    if (!isQuizLoaded) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("문제 유형: 삽입", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier
            .weight(1f)
            .padding(bottom = 8.dp)) {
            items(fullSentences.size) { index ->
                if (insertIndices.contains(index)) {
                    val selectedIdx = userSelectedMap[index]
                    val text = insertionChoices.firstOrNull { it.first == selectedIdx }?.second ?: "(문장 선택)"
                    val isSelected = selectedTarget == index
                    Button(
                        onClick = { selectedTarget = index },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .border(2.dp, if (isSelected) Color.Blue else Color.Gray, RoundedCornerShape(6.dp))
                    ) {
                        Text(text)
                    }
                } else {
                    Text(fullSentences[index], modifier = Modifier.padding(8.dp))
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    currentChoiceIndex = (currentChoiceIndex - 1 + insertionChoices.size) % insertionChoices.size
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("<")
            }

            Spacer(modifier = Modifier.width(8.dp))

            val (idx, sentence) = insertionChoices[currentChoiceIndex]
            val alreadyUsed = userSelectedMap.containsValue(idx)
            val assignedKey = userSelectedMap.entries.find { it.value == idx }?.key
            val isSelected = userSelectedMap.containsValue(idx)

            Button(
                onClick = {
                    if (assignedKey != null) {
                        userSelectedMap.remove(assignedKey)
                    } else if (selectedTarget != null && !alreadyUsed) {
                        userSelectedMap[selectedTarget!!] = idx
                        selectedTarget = null
                    }
                },
                modifier = Modifier
                    .weight(4f)
                    .heightIn(min = 60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) Color(0xFF6D9886) else Color(0xFFE0E0E0)
                )
            ) {
                Text(sentence, color = Color.White)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    currentChoiceIndex = (currentChoiceIndex + 1) % insertionChoices.size
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(">")
            }
        }
        /*
        Column(modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())) {

            Button(onClick = { isChoiceExpanded = !isChoiceExpanded }) {
                Text(if (isChoiceExpanded) "보기 접기" else "보기 펼치기")
            }

            if (isChoiceExpanded) {
                insertionChoices.chunked(5).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        row.forEach { (originalIdx, sentence) ->
                            val alreadyUsed = userSelectedMap.containsValue(originalIdx)
                            val assignedKey = userSelectedMap.entries.find { it.value == originalIdx }?.key

                            Button(
                                onClick = {
                                    if (assignedKey != null) {
                                        userSelectedMap.remove(assignedKey)
                                        selectedChoices = selectedChoices.filterNot { it == originalIdx }
                                    } else if (selectedTarget != null && !alreadyUsed) {
                                        userSelectedMap[selectedTarget!!] = originalIdx
                                        selectedChoices = selectedChoices + originalIdx
                                        selectedTarget = null
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .heightIn(min = 60.dp)
                            ) {
                                Text(
                                    text = sentence,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }

         */

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                val userAnswers = insertIndices.map { index ->
                    userSelectedMap[index] ?: -1  // 선택 안 한 경우 -1로
                }
                //val userAnswers = insertIndices.mapNotNull { userSelectedMap[it] }.toMutableList()
                viewModel.userAnswers = userAnswers.toMutableList()
                //viewModel.userAnswers = selectedChoices.toMutableList()
                viewModel.insertNumList = insertIndices.toMutableList()

                scope.launch {
                    try {
                        val quizId = viewModel.saveQuizResult(
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId,
                            quizType = "insertion" // 삽입 퀴즈 유형 지정
                        )
                        navController.navigate("insertion_result/$userId/$contentType/$contentId/$quizId")
                    } catch (e: Exception) {
                        Toast.makeText(context, "퀴즈 저장 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = true
        ) {
            Text("제출")
        }
    }
}





