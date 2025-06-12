package com.example.learningenglish.ui.recommendation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.viewmodel.LearningViewModel
import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextLibraryScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadLibrary()
    }

    val libraryItems = viewModel.userLibrary.collectAsState().value

    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    val coroutineScope = rememberCoroutineScope()

    val userLibrary = viewModel.userLibrary.collectAsState().value
    val myTextContents = userLibrary.filter { it.contentType == "text"}

    LaunchedEffect(Unit) {
        userPrefs.getUserId().collectLatest { userId ->
            if (userId != null) {
                viewModel.loadLibraryForUser(userId)
            }
        }
    }



    Scaffold(topBar = {
        TopAppBar(
            title = { Text("내가 등록한 텍스트") },
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
            items(myTextContents) { content ->
                val percent = content.progress.roundToInt()
                //val dummyPercent = (10..90).random()
                VideoLibraryCard(content, percent) {
                    navController.navigate("select_mode/text/${content.contentId}")
                }
            }
            }
        }
    }
}




