package com.example.learningenglish.ui.recommendation

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.data.model.UserLibraryContent
import com.example.learningenglish.viewmodel.FilterOption
import com.example.learningenglish.viewmodel.LearningViewModel
import com.example.learningenglish.viewmodel.SortOption
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import kotlinx.coroutines.flow.collectLatest

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsPressedAsState

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navController: NavController,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val userPrefs = UserPreferencesDataStore(context)
    val coroutineScope = rememberCoroutineScope()
    var userId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().firstOrNull()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 라이브러리") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("home") }) {
                        Icon(Icons.Default.Close, contentDescription = "home")
                    }
                }
            )
        },
        containerColor = Color(0xFFFAFAFA)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 36.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(36.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LibraryMainButton("📝 텍스트 콘텐츠") {
                navController.navigate("mytexts")
            }
            LibraryMainButton("🎥 영상 콘텐츠") {
                navController.navigate("myvideos")
            }
        }
    }
}

@Composable
fun LibraryMainButton(text: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .shadow(if (isPressed) 8.dp else 2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) Color(0xFFB3E5FC) else Color(0xFF81D4FA),
            contentColor = Color.Black
        ),
        interactionSource = interactionSource
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}


@Composable
fun VideoLibraryCard(
    content: UserLibraryContent,
    progressPercent: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = content.userTitle ?: "제목 없음", style = MaterialTheme.typography.titleMedium)
                    Text(text = content.title ?: "유튜브 제목", style = MaterialTheme.typography.bodySmall)
                }

                Box(
                    modifier = Modifier.size(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = progressPercent / 100f,
                        strokeWidth = 6.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 하단 진행도 3개
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgressBadge("받아쓰기", (content.writeScore?.toFloat() ?: 0f) / 100f)
                ProgressBadge("발음 평가", (content.speechScore?.toFloat() ?: 0f) / 100f)
                ProgressBadge("퀴즈", (content.quizScore?.toFloat() ?: 0f) / 100f)

            }
        }
    }
}

@Composable
fun ProgressBadge(label: String, progress: Float) {
    val scoreText = String.format("%.1f", progress * 100) // 70.7처럼 표현

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LinearProgressIndicator(progress = progress, modifier = Modifier.width(80.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "$label: 평균 ${scoreText}점", style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreenWithSearch(
    navController: NavController,
    userId: Int,
    searchResults: List<Pair<String, Int>>,
    onSearch: (String, Float, Float) -> Unit,
    viewModel: LearningViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 검색 관련 상태
    var queryText by remember { mutableStateOf("") }
    var difficultyRange by remember { mutableStateOf(1f..15f) }

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
        ) {
            // 🔍 검색 UI
            OutlinedTextField(
                value = queryText,
                onValueChange = { queryText = it },
                label = { Text("주제 텍스트 입력") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text("난이도 범위: ${difficultyRange.start} ~ ${difficultyRange.endInclusive}")
            RangeSlider(
                value = difficultyRange,
                onValueChange = { difficultyRange = it },
                valueRange = 1f..15f
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                onSearch(
                    queryText,
                    difficultyRange.start,
                    difficultyRange.endInclusive
                )
            }) {
                Text("검색")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 🔁 검색 결과 목록
            LazyColumn (
                modifier = Modifier.fillMaxWidth(),
            ){
                item {
                    Text("검색 결과:", style = MaterialTheme.typography.titleMedium)
                }
                items(items = searchResults,
                    key = { "${it.first}_${it.second}" }
                ) { item ->
                    val contentType = item.first
                    val contentId = item.second

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Type: $contentType / ID: $contentId")

                        Button(onClick = {
                            coroutineScope.launch {
                                viewModel.addContentToLibrary(contentType, contentId, userId)
                            }
                        }) {
                            Text("라이브러리에 추가")
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    LibraryMainButton("📖 전체 텍스트 보기") {
                        navController.navigate("alltexts")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LibraryMainButton("🎬 전체 영상 보기") {
                        navController.navigate("allvideos")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LibraryMainButton("📝 내 텍스트 보기") {
                        navController.navigate("mytexts")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LibraryMainButton("🎥 내 영상 보기") {
                        navController.navigate("myvideos")
                    }
                }
            }
        }
}






