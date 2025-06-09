package com.example.learningenglish.ui.recommendation

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimilarContentScreen(
    contentType: String,
    contentId: Int,
    navController: NavController,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    val coroutineScope = rememberCoroutineScope()

    var userId by remember { mutableStateOf(0) }
    var queryText by remember { mutableStateOf("") }
    var difficultyRange by remember { mutableStateOf(1f..15f) }
    val searchResults by viewModel.similarContents.collectAsState()
    //var searchResults by remember { mutableStateOf(emptyList<Pair<String, Int>>()) }

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().firstOrNull() ?: 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("유사 콘텐츠 검색") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize()
        ) {

            Text("난이도: ${difficultyRange.start} ~ ${difficultyRange.endInclusive}")
            RangeSlider(
                value = difficultyRange,
                onValueChange = { difficultyRange = it },
                valueRange = 1f..15f
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                userId?.let { uid ->
                    coroutineScope.launch {
                        viewModel.loadSimilarContents(contentType, contentId, userId, difficultyRange.start, difficultyRange.endInclusive)
                    }
                }
            }) {
                Text("검색")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(searchResults) { (contentType, contentId) ->
                    SearchResultCard(
                        contentType = contentType,
                        contentId = contentId,
                        userId = userId,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
