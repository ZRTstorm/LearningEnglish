package com.example.learningenglish.ui.quiz

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun InsertionQuizScreen(
    navController: NavController,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel // 기존 ViewModel 주입받음
) {
    val context = LocalContext.current

    // 퀴즈 데이터를 처음 한 번만 불러옴
    LaunchedEffect(Unit) {
        viewModel.loadQuiz(contentType, contentId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("빈칸 채우기 문제", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(viewModel.sentenceList.size) { i ->
                if (viewModel.insertNumList.contains(i)) {
                    val indexInInsertList = viewModel.insertNumList.indexOf(i)
                    QuizBlankBox(
                        options = viewModel.insertNumList.shuffled().map { viewModel.sentenceList[it] },
                        onOptionSelected = { selectedIndex ->
                            viewModel.setUserAnswer(indexInInsertList, selectedIndex)
                        }
                    )
                } else {
                    Text(text = viewModel.sentenceList[i], modifier = Modifier.padding(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val score = viewModel.calculateScore()
            Toast.makeText(context, "점수: $score 점", Toast.LENGTH_SHORT).show()
            viewModel.saveQuiz(contentType, contentId)
        }) {
            Text("제출")
        }
    }
}


@Composable
fun QuizBlankBox(
    options: List<String>,
    onOptionSelected: (Int) -> Unit
) {
    var selectedIndex by remember { mutableStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.Gray, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Text("아래 보기 중 하나를 선택하세요", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(8.dp))
        options.forEachIndexed { index, option ->
            val background = if (selectedIndex == index) Color.LightGray else Color.Transparent
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
                    .background(background, shape = RoundedCornerShape(4.dp))
                    .clickable {
                        selectedIndex = index
                        onOptionSelected(index)  // 중요한 부분!
                    }
                    .padding(8.dp)
            ) {
                Text(text = option)
            }
        }
    }
}

@Composable
fun InsertionQuizDragScreen(
    navController: NavController,
    contentType: String,
    contentId: Int,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var options by remember { mutableStateOf(listOf<Pair<Int, String>>()) }
    val reorderState = rememberReorderableLazyListState(onMove = { from, to ->
        options = options.toMutableList().apply { add(to.index, removeAt(from.index)) }
    })

    LaunchedEffect(Unit) {
        viewModel.loadQuiz(contentType, contentId)
        options = viewModel.insertNumList.shuffled().map { it to viewModel.sentenceList[it] }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("빈칸 보기 순서 배열 퀴즈", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            state = reorderState.listState,
            modifier = Modifier
                .fillMaxWidth()
                .reorderable(reorderState)
                .detectReorderAfterLongPress(reorderState)
        ) {
            items(options, key = { it.first }) { (index, sentence) ->
                ReorderableItem(reorderState, key = index) {
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
            val selectedIds = options.map { it.first }
            viewModel.userAnswers = selectedIds.toMutableList()
            val score = viewModel.calculateScore()
            Toast.makeText(context, "점수: $score 점", Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                viewModel.saveQuizResult(contentType, contentId)
            }
        }) {
            Text("제출")
        }
    }
}

 */


