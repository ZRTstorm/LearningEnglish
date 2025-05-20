package com.example.learningenglish.ui.learning.vocabulary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.learningenglish.viewmodel.LearningViewModel

@Composable
fun VocabScreen(viewModel: LearningViewModel, uid: String) {
    var page by remember { mutableStateOf(0) }

    val vocabList by viewModel.pagedUserVocab.collectAsState()

    LaunchedEffect(page) {
        viewModel.loadUserVocabPaged(uid, page)
    }

    Column {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(vocabList) { word ->
                Text(word.word, style = MaterialTheme.typography.titleMedium)
                word.definitions.forEach {
                    Text("뜻: ${it.definitionKo}")
                    Text("예시: ${it.exampleKo}", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { if (page > 0) page-- }) {
                Text("이전")
            }
            Button(onClick = { page++ }) {
                Text("다음")
            }
        }
    }
}
