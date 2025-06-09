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

@Composable
fun MixedQuizScreen(
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var quizType by remember { mutableStateOf(if ((0..1).random() == 0) "insertion" else "summaOrders") }
    var showScore by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var quizId by remember { mutableStateOf(-1) }

    var isQuizLoaded by remember { mutableStateOf(false) }

    var fullSentences by remember { mutableStateOf(listOf<String>()) }
    var insertIndices by remember { mutableStateOf(listOf<Int>()) }
    var insertionChoices by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var userSelectedMap by remember { mutableStateOf(mutableMapOf<Int, Int>()) }

    var sentenceList by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var selectedOrder by remember { mutableStateOf(listOf<Pair<Int, String>>()) }

    var feedbackOriginal by remember { mutableStateOf(listOf<String>()) }
    var feedbackUser by remember { mutableStateOf(listOf<String>()) }

    val isInsertionCompleted by remember(userSelectedMap) {
        derivedStateOf { insertIndices.all { userSelectedMap.containsKey(it) } }
    }
    var isRetry by remember { mutableStateOf(false) }

    val isOrderCompleted by remember(selectedOrder, sentenceList) {
        derivedStateOf { selectedOrder.size == sentenceList.size }
    }

    fun resetQuiz(sameQuiz: Boolean) {
        showScore = false
        score = 0
        userSelectedMap.clear()
        selectedOrder = listOf()
        feedbackOriginal = listOf()
        feedbackUser = listOf()
        isQuizLoaded = false
        isRetry = sameQuiz
        if (!sameQuiz) quizType = if (Random.nextBoolean()) "insertion" else "summaOrders"
    }

    LaunchedEffect(quizType) {
        try {
            if (quizType == "insertion") {
                val quizData = viewModel.loadQuiz(userId, contentType, contentId).also {
                    quizId = it.quizId
                }
                fullSentences = quizData.sentenceList
                insertIndices = viewModel.insertNumList
                insertionChoices = insertIndices.map { it to fullSentences[it] }.shuffled()
            } else {
                val quizData = viewModel.loadOrderQuiz(userId, contentType, contentId).also {
                    quizId = it.quizId
                }
                sentenceList = quizData.sentenceList.mapIndexed { i, s -> i to s }.shuffled()
            }
            isQuizLoaded = true
        } catch (e: Exception) {
            Toast.makeText(context, "퀴즈 로딩 실패", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            if (!showScore) {
                Button(
                    onClick = {
                        scope.launch {
                            val scoreVal = if (quizType == "insertion") {
                                viewModel.userAnswers = insertIndices.mapNotNull {
                                    userSelectedMap[it]  // 이미 원래 인덱스가 저장됨
                                }.toMutableList()
                                viewModel.calculateScore().also {
                                    viewModel.saveQuizResult(userId, contentType, contentId)
                                }
                            } else {
                                viewModel.userAnswers = selectedOrder.map { it.first }.toMutableList()
                                viewModel.calculateOrderScore().also {
                                    viewModel.saveOrderQuizResult(userId, contentType, contentId)
                                }
                            }
                            score = scoreVal
                            showScore = true

                            if (quizType == "insertion") {
                                val insertion = viewModel.loadInsertionFeedback(quizId)
                                feedbackOriginal = insertion.originalNumList.map { insertion.sentenceList[it] }
                                feedbackUser = insertion.userNumList.mapNotNull { idx ->
                                    insertion.sentenceList.getOrNull(idx)
                                }
                            } else {
                                val order = viewModel.loadOrderFeedback(quizId)
                                feedbackOriginal = order.originalText.map { it.text }
                                feedbackUser = order.userOrders.mapNotNull { idx ->
                                    order.originalText.firstOrNull { s -> s.index == idx }?.text
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    enabled = (quizType == "insertion" && isInsertionCompleted) ||
                            (quizType == "summaOrders" && isOrderCompleted)
                ) {
                    Text("제출")
                }
            }
        }
    ) { padding ->
        if (!isQuizLoaded) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("문제 유형: ${if (quizType == "insertion") "삽입" else "순서 배열"}", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                if (quizType == "insertion") {
                    items(fullSentences.size) { index ->
                        if (insertIndices.contains(index)) {
                            val selectedIdx = userSelectedMap[index]
                            val text = insertionChoices.firstOrNull { it.first == selectedIdx }?.second ?: "(문장 선택)"
                            Button(onClick = {}, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                                Text(text)
                            }
                        } else {
                            Text(fullSentences[index], modifier = Modifier.padding(8.dp))
                        }
                    }
                    item {
                        Spacer(Modifier.height(16.dp))
                        Text("보기 문장 선택")
                        insertionChoices.forEach { (originalIdx, sentence) ->
                            if (!userSelectedMap.containsValue(originalIdx)) {
                                Button(onClick = {
                                    val target = insertIndices.firstOrNull { !userSelectedMap.containsKey(it) }
                                    if (target != null) userSelectedMap[target] = originalIdx
                                }) { Text(sentence) }
                            }
                        }
                    }
                } else {
                    items(sentenceList) { pair ->
                        Button(onClick = {
                            if (!selectedOrder.contains(pair)) selectedOrder = selectedOrder + pair
                        }, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                            Text(pair.second)
                        }
                    }
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text("선택한 순서")
                        selectedOrder.forEachIndexed { i, pair ->
                            Text("${i + 1}. ${pair.second}", modifier = Modifier.padding(4.dp))
                        }
                    }
                }

                if (showScore) {
                    item {
                        Text("🎯 점수: $score 점", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(12.dp))
                        Text("📚 정답 순서")
                        feedbackOriginal.forEach { Text("✔ $it") }
                        Spacer(Modifier.height(8.dp))
                        Text("🙋 사용자 순서")
                        feedbackUser.forEach { Text("👉 $it") }
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            if (quizId != -1) {
                                navController.navigate("quiz_history/$userId/$contentType/$contentId?latestQuizId=$quizId")
                            } else {
                                navController.navigate("quiz_history/$userId/$contentType/$contentId")
                            }
                        }) { Text("복습하기") }
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { resetQuiz(sameQuiz = false) }) { Text("다음 문제로") }
                    }
                }
            }
        }
    }
}








/*
@Composable
fun MixedQuizScreen(
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel
) {

    val coroutineScope = rememberCoroutineScope()

    var quizType by remember { mutableStateOf(if (Random.nextBoolean()) "insertion" else "order") }
    var showScore by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }

    var items by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        items = items.toMutableList().apply { add(to.index, removeAt(from.index)) }
    })
    var quizId by remember { mutableIntStateOf(0) }

    val context = LocalContext.current
    val userPrefs = UserPreferencesDataStore(context)

    // 배열 퀴즈용 상태
    var orderedItems by remember { mutableStateOf(listOf<Pair<Int, String>>()) }

    // 삽입 퀴즈용 상태
    var fullSentences by remember { mutableStateOf(listOf<String>()) }
    var insertIndices by remember { mutableStateOf(listOf<Int>()) }
    var insertionChoices by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var userSelectedMap by remember { mutableStateOf(mutableMapOf<Int, Int>()) } // 삽입 위치 -> 선택한 문장 인덱스

    val isInsertionCompleted by remember(userSelectedMap) {
        derivedStateOf {
            insertIndices.all { userSelectedMap.containsKey(it) }
        }
    }
    //val isInsertionCompleted = insertIndices.all { userSelectedMap.containsKey(it) }
    val isOrderCompleted = orderedItems.isNotEmpty()


    LaunchedEffect(quizType) {
        try {
            val userId = userPrefs.getUserId().first() ?: 0
            if (quizType == "insertion") {
                val result = viewModel.loadQuiz(userId, contentType, contentId)
                quizId = result.quizId
                fullSentences = viewModel.sentenceList
                insertIndices = viewModel.insertNumList
                insertionChoices = insertIndices.map { it to fullSentences[it] }.shuffled()
            } else {
                val result = viewModel.loadOrderQuiz(userId, contentType, contentId)
                quizId = result.quizId
                items = result.sentenceList.shuffledIndexedPairs()
                orderedItems = items
            }
        } catch (e: Exception) {
            Log.e("QuizLoad", "퀴즈 로딩 실패: ${e.localizedMessage}")
            Toast.makeText(context, "퀴즈 불러오기 실패 (서버 응답 오류)", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            if (!showScore) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val userId = userPrefs.getUserId().first() ?: return@launch
                            score = if (quizType == "insertion") {
                                viewModel.userAnswers = insertIndices.mapNotNull { insertIdx ->
                                    val selectedChoiceIdx = userSelectedMap[insertIdx]
                                    insertionChoices.getOrNull(selectedChoiceIdx ?: -1)?.first
                                }.toMutableList()
                                viewModel.calculateScore().also {
                                    viewModel.saveQuizResult(userId, contentType, contentId)
                                }
                            } else {
                                viewModel.userAnswers = orderedItems.map { it.first }.toMutableList()
                                viewModel.calculateOrderScore().also {
                                    viewModel.saveOrderQuizResult(userId, contentType, contentId)
                                }
                            }
                            showScore = true
                            Toast.makeText(context, "점수: $score 점", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text("제출")
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(
                text = if (quizType == "insertion") "문장 삽입 퀴즈" else "문장 순서 배열 퀴즈",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (quizType == "insertion") {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(fullSentences.indices.toList()) { index ->
                        if (insertIndices.contains(index)) {
                            val selected = userSelectedMap[index]
                            val text = if (selected != null) {
                                insertionChoices.firstOrNull { it.first == selected }?.second ?: "(문장 선택)"
                            } else {
                                "(문장 선택)"
                            }
                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth().padding(4.dp)
                            ) {
                                Text(text)
                            }
                        } else {
                            Text(
                                text = fullSentences[index],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                    .padding(12.dp)
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("보기 문장 선택")
                        insertionChoices.forEach { (idx, sentence) ->
                            if (!userSelectedMap.containsValue(idx)) {
                                Button(onClick = {
                                    val target = insertIndices.firstOrNull { it !in userSelectedMap.keys }
                                    if (target != null) {
                                        userSelectedMap = userSelectedMap.toMutableMap().apply {
                                            this[target] = idx
                                        }
                                    }
                                }, modifier = Modifier.padding(4.dp)) {
                                    Text(sentence)
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(orderedItems, key = { it.first }) { (_, sentence) ->
                        Text(
                            text = sentence,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(12.dp)
                        )
                    }
                }
            }

            if (showScore) {
                Text("🎯 점수: $score 점", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        val feedbackData = if (quizType == "insertion") {
                            viewModel.loadInsertionFeedback(quizId)
                        } else {
                            viewModel.loadOrderFeedback(quizId)
                        }
                        orderedItems = feedbackData
                        showScore = false
                        score = 0
                    }
                }) {
                    Text("피드백 보기 및 재도전")
                }
            }
        }
    }
}

    /*
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = if (quizType == "insertion") "문장 삽입 퀴즈" else "문장 순서 배열 퀴즈",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (quizType == "insertion") {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(fullSentences.indices.toList()) { index ->
                    if (insertIndices.contains(index)) {
                        val selected = userSelectedMap[index]
                        val text = if (selected != null) {
                            val selectedIdx = selected
                            insertionChoices.firstOrNull { it.first == selectedIdx }?.second ?: "(문장 선택)"
                        } else {
                            "(문장 선택)"
                        }
                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Text(text)
                        }
                    } else {
                        Text(
                            text = fullSentences[index],
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(12.dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("보기 문장 선택")
                    insertionChoices.forEach { (idx, sentence) ->
                        if (!userSelectedMap.containsValue(idx)) {
                            Button(onClick = {
                                // 아직 선택하지 않은 빈칸 중 하나에 매핑
                                val target = insertIndices.firstOrNull { it !in userSelectedMap.keys }
                                //if (target != null) userSelectedMap[target] = idx
                                if (target != null) {
                                    userSelectedMap = userSelectedMap.toMutableMap().apply {
                                        this[target] = idx
                                    }
                                }
                            }, modifier = Modifier.padding(4.dp)) {
                                Text(sentence)
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(orderedItems, key = { it.first }) { (_, sentence) ->
                    Text(
                        text = sentence,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                            .padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!showScore && (quizType == "order" && isOrderCompleted || quizType == "insertion" && isInsertionCompleted))  {
            Button(onClick = {
                val answerOrder = items.map { it.first }
                //viewModel.userAnswers = answerOrder.toMutableList()

                coroutineScope.launch {
                    val userId = userPrefs.getUserId().first() ?: return@launch
                    score = if (quizType == "insertion") {

                        val original = insertIndices.joinToString("-")
                        //val user = insertIndices.map { userSelectedMap[it] ?: -1 }.joinToString("-")
                        val user = insertIndices.mapNotNull { insertIdx ->
                            val selectedChoiceIdx = userSelectedMap[insertIdx] // 삽입 위치에 대해 사용자가 고른 보기 인덱스
                            insertionChoices.getOrNull(selectedChoiceIdx ?: -1)?.first // → 원래 문장의 인덱스 (ex: 2, 1, 9 ...)
                        }.joinToString("-")

                        viewModel.userAnswers = insertIndices.mapNotNull { insertIdx ->
                            val selectedChoiceIdx = userSelectedMap[insertIdx]
                            insertionChoices.getOrNull(selectedChoiceIdx ?: -1)?.first // 실제 문장 인덱스 (ex: 2, 12, 9 등)
                        }.toMutableList()
                        
                        val scoreVal = viewModel.calculateScore()
                        
                        viewModel.saveQuizResult(userId, contentType, contentId)
                        scoreVal
                    } else {
                        viewModel.userAnswers = orderedItems.map { it.first }.toMutableList()
                        /*
                        viewModel.userAnswers = insertIndices.mapNotNull { insertIdx ->
                            val selectedChoiceIdx = userSelectedMap[insertIdx]
                            insertionChoices.getOrNull(selectedChoiceIdx ?: -1)?.first // 실제 문장 인덱스 (ex: 2, 12, 9 등)
                        }.toMutableList()
                         */
                        viewModel.calculateOrderScore().also {
                            viewModel.saveOrderQuizResult(userId, contentType, contentId)
                        }
                    }
                    showScore = true
                    Toast.makeText(context, "점수: $score 점", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("제출")
            }
        } else {
            Text("🎯 점수: $score 점", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                coroutineScope.launch {
                    val feedbackData = if (quizType == "insertion") {
                        viewModel.loadInsertionFeedback(quizId)
                    } else {
                        viewModel.loadOrderFeedback(quizId)
                    }
                    orderedItems = feedbackData
                    showScore = false
                    score = 0
                }
            }) {
                Text("피드백 보기 및 재도전")
            }
        }
    }
}

 */

     */

private fun List<String>.shuffledIndexedPairs(): List<Pair<Int, String>> =
    this.mapIndexed { i, s -> i to s }.shuffled()



