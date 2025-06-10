package com.example.learningenglish.ui.quiz

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTypeSelectScreen(
    navController: NavController,
    userId: Int,
    contentType: String,
    contentId: Int
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("퀴즈 유형 선택") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navController.navigate("insertion_quiz/$userId/$contentType/$contentId")
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("삽입 퀴즈 시작하기")
            }

            Button(
                onClick = {
                    navController.navigate("order_quiz/$userId/$contentType/$contentId")
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            ) {
                Text("배열 퀴즈 시작하기")
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
    var selectedTarget by remember { mutableStateOf<Int?>(null) }
    var isChoiceExpanded by remember { mutableStateOf(false) }

    var sentenceList by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    var selectedOrder by remember { mutableStateOf(listOf<Pair<Int, String>>()) }

    var feedbackOriginal by remember { mutableStateOf(listOf<String>()) }
    var feedbackUser by remember { mutableStateOf(listOf<String>()) }

    val isInsertionCompleted by remember(userSelectedMap) {
        derivedStateOf { insertIndices.all { userSelectedMap.containsKey(it) } }
    }

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
        selectedTarget = null
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
                insertionChoices = insertIndices.map { index ->
                    index to quizData.sentenceList[index]
                }.shuffled()
            } else {
                val quizData = viewModel.loadOrderQuiz(userId, contentType, contentId).also {
                    quizId = viewModel.saveOrderQuizResult(userId, contentType, contentId)
                }
                sentenceList = quizData.sentenceList.map { it.index to it.text }.shuffled()
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
                                viewModel.userAnswers = insertIndices.mapNotNull { userSelectedMap[it] }.toMutableList()
                                viewModel.insertNumList = insertIndices.toMutableList()
                                viewModel.calculateScore().also {
                                    quizId = viewModel.saveQuizResult(userId, contentType, contentId, quizType)
                                }
                            } else {
                                viewModel.userAnswers = selectedOrder.map { it.first }.toMutableList()
                                viewModel.calculateOrderScore().also {
                                    quizId = viewModel.saveOrderQuizResult(userId, contentType, contentId)
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
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
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

            if (quizType == "insertion") {
                LazyColumn(modifier = Modifier.weight(1f).padding(bottom = 8.dp)) {
                    items(fullSentences.size) { index ->
                        if (insertIndices.contains(index)) {
                            val selectedIdx = userSelectedMap[index]
                            val text = insertionChoices.firstOrNull { it.first == selectedIdx }?.second ?: "(문장 선택)"
                            Button(
                                onClick = { selectedTarget = index },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                                    .border(1.dp, if (selectedTarget == index) Color.Blue else Color.Gray, RoundedCornerShape(6.dp))
                            ) {
                                Text(text)
                            }
                        } else {
                            Text(fullSentences[index], modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Button(onClick = { isChoiceExpanded = !isChoiceExpanded }) {
                        Text(if (isChoiceExpanded) "보기 접기" else "보기 펼치기")
                    }
                    if (isChoiceExpanded) {
                        insertionChoices.chunked(5).forEach { row ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                row.forEach { (originalIdx, sentence) ->
                                    val alreadyUsed = userSelectedMap.containsValue(originalIdx)
                                    val assignedKey = userSelectedMap.entries.find { it.value == originalIdx }?.key
                                    val isSelected = selectedTarget != null && !alreadyUsed

                                    Button(
                                        onClick = {
                                            if (assignedKey != null) {
                                                userSelectedMap.remove(assignedKey)
                                            } else if (selectedTarget != null && !alreadyUsed) {
                                                userSelectedMap[selectedTarget!!] = originalIdx
                                                selectedTarget = null
                                            }
                                        },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(4.dp)
                                            .border(1.dp, if (isSelected) Color.Green else Color.Gray, RoundedCornerShape(4.dp))
                                    ) {
                                        Text(sentence)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
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
            }

            if (showScore) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("🎯 점수: $score 점", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(12.dp))
                    Text("정답 순서")
                    feedbackOriginal.forEach { Text("✔ $it") }
                    Spacer(Modifier.height(8.dp))
                    Text("사용자 순서")
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

 */


private fun List<String>.shuffledIndexedPairs(): List<Pair<Int, String>> =
    this.mapIndexed { i, s -> i to s }.shuffled()



