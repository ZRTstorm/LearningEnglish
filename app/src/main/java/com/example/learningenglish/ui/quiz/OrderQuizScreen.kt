package com.example.learningenglish.ui.quiz

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

/*
@Composable
fun OrderQuizScreen(
    navController: NavController,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var currentOrder by remember { mutableStateOf(listOf<Int>()) }

    LaunchedEffect(Unit) {
        viewModel.loadOrderQuiz(contentType, contentId)
        currentOrder = viewModel.userAnswers
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("문장 순서 배열 퀴즈", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(currentOrder.size) { i ->
                val idx = currentOrder[i]
                val text = viewModel.sentenceList[viewModel.insertNumList.indexOf(idx)]
                Text(
                    text = "${i + 1}. $text",
                    modifier = Modifier
                        .padding(8.dp)
                        .border(1.dp, Color.Gray)
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val score = viewModel.calculateOrderScore()
            Toast.makeText(context, "점수: $score 점", Toast.LENGTH_SHORT).show()

            coroutineScope.launch {
                viewModel.saveOrderQuizResult(contentType, contentId)
            }
        }) {
            Text("제출")
        }
    }
}

@Composable
fun OrderQuizDragScreen(
    navController: NavController,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var orderList by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        orderList = orderList.toMutableList().apply { add(to.index, removeAt(from.index)) }
    })

    LaunchedEffect(Unit) {
        viewModel.loadOrderQuiz(contentType, contentId)
        orderList = viewModel.insertNumList.mapIndexed { i, idx -> idx to viewModel.sentenceList[i] }.shuffled()
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("문장 순서 배열 퀴즈", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            state = reorderState.listState,
            modifier = Modifier
                .fillMaxWidth()
                .reorderable(reorderState)
                .detectReorderAfterLongPress(reorderState)
        ) {
            items(orderList, key = { it.first }) { (idx, sentence) ->
                ReorderableItem(reorderState, key = idx) {
                    Text(
                        text = sentence,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(1.dp, Color.Gray)
                            .padding(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val answerOrder = orderList.map { it.first }
            viewModel.userAnswers = answerOrder.toMutableList()
            val score = viewModel.calculateOrderScore()
            Toast.makeText(context, "점수: $score 점", Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                viewModel.saveOrderQuizResult(contentType, contentId)
            }
        }) {
            Text("제출")
        }
    }
}

 */

