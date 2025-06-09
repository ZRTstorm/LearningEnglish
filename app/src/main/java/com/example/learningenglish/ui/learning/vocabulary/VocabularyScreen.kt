package com.example.learningenglish.ui.learning.vocabulary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.learningenglish.data.model.WordDetailResponse
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.first

@Composable
fun VocabScreen(viewModel: LearningViewModel, userId: Int) {
    var page by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    var userId by remember { mutableStateOf(0) }
    val vocabList by viewModel.pagedUserVocab.collectAsState()

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().first() ?: 0
    }

    LaunchedEffect(page) {
        viewModel.loadUserVocabPaged(userId, page)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(vocabList) { word ->
                WordCard(word)
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

@Composable
fun WordCard(word: WordDetailResponse) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
            .clickable { expanded = !expanded },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(
                text = word.word,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black //Color(0xFF283593) // Deep Indigo
            )
            word.audioUrl?.let { url ->
                IconButton(onClick = { //playAudio(url)
                }) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "발음 재생"
                    )
                }
            }
        }

        if (expanded) {
            word.phonetic?.let {
                Text(
                    text = "발음: $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            word.definitions.forEach { def ->
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "의미: (${def.partOfSpeech}) ${def.definitionKo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    def.definitionEn?.let {
                        Text(
                            text = "English: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF5C6BC0),
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                    def.exampleKo?.let {
                        Text(
                            text = "예문: $it",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF607D8B),
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
