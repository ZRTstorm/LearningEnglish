package com.example.learningenglish.ui.recommendation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

// MyVideoLibraryScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyVideoLibraryScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }

    LaunchedEffect(Unit) {
        viewModel.loadLibrary()
    }

    val libraryItems = viewModel.userLibrary.collectAsState().value

    LaunchedEffect(Unit) {
        userPrefs.getUserId().collectLatest { userId ->
            if (userId != null) {
                viewModel.loadLibraryForUser(userId)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.initRepository(context.applicationContext)
    }

    val coroutineScope = rememberCoroutineScope()

    val userLibrary = viewModel.userLibrary.collectAsState().value
    val myVideoContents = userLibrary.filter { it.contentType == "video"}

    val progressMap by viewModel.progressMap.collectAsState()

    LaunchedEffect(userLibrary.map { it.contentId }) {
        userLibrary
            .filter { it.contentType == "video" }
            .forEach { content ->
                if (!progressMap.containsKey(content.contentId)) {
                    //progressMap[content.contentId] == null
                    viewModel.setProgressForContent(content.contentId, 0)
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.loadSavedProgress()
    }



    Scaffold(topBar = {
        TopAppBar(
            title = { Text("내가 등록한 영상") },
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
        if (libraryItems.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("등록된 콘텐츠가 없습니다.")
            }
        } else {
            LazyColumn(contentPadding = padding) {
                items(myVideoContents) { content ->
                    val percent = content.progress.roundToInt()
                    //val dummyPercent = (10..90).random()
                    VideoLibraryCard(content, percent) {
                        navController.navigate("select_mode2/video/${content.contentId}")
                    }
                }
            }
        }
    }
}
