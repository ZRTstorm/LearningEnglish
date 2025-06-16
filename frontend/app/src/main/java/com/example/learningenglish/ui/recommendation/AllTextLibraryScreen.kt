package com.example.learningenglish.ui.recommendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTextLibraryScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    val allTexts by viewModel.textLibrary.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAllTextContents(maxId=6)
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("전체 텍스트 콘텐츠") })
    }) { padding ->
        if (allTexts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(allTexts) { text ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("select_mode/text/${text.contentId}")
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = text.title, style = MaterialTheme.typography.titleMedium)
                            Text("난이도: ${text.textGrade} / 발음: ${text.soundGrade}")
                        }
                    }
                }
            }
        }
    }
}
