package com.example.learningenglish.ui.learning.DataLearning.main

import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.learningenglish.data.model.SubtitleSentence
import com.example.learningenglish.ui.Word.WordDetailDialog
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.filled.Bookmark
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    contentsType: String,
    contentId: Int
) {
    val videoDetail by viewModel.videoDetail.collectAsState()
    var subtitleMode by remember { mutableStateOf("BOTH") }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var seekToMillis by remember { mutableStateOf<Float?>(null) }
    var highlightedMillis by remember { mutableStateOf<Long?>(null) }

    var selectedWord by remember { mutableStateOf("") }
    val wordInfo by viewModel.selectedWordInfo.collectAsState()
    var showWordDialog by remember { mutableStateOf(false) }

    var showExitDialog by remember { mutableStateOf(false) }

    val difficultyRange by remember { mutableStateOf(1f..15f) }

    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    var userId by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        viewModel.initRepository(context.applicationContext)
    }

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().first() ?: 0
    }

    LaunchedEffect(contentId) {
        viewModel.loadVideoDetail(contentId)
    }

    // VideoDetailScreen.kt에서 진도율 계산해서 ViewModel에 저장
    LaunchedEffect(videoDetail) {
        videoDetail?.let { detail ->
            val subtitleSentences = detail.sentences.map {
                SubtitleSentence(
                    originalText = it.originalText,
                    translatedText = it.translatedText,
                    startTimeMillis = it.startTimeMillis,
                    bookmarked = false  // 이 부분은 사용자가 선택 후 업데이트 가능
                )
            }

            val totalSentences = subtitleSentences.size
            val bookmarkedIndex = subtitleSentences.indexOfFirst { it.bookmarked }
            val progressPercent = if (bookmarkedIndex >= 0) {
                ((bookmarkedIndex + 1).toFloat() / totalSentences * 100).toInt()
            } else 0

            viewModel.loadBookmarkedProgress(contentId, subtitleSentences)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("영상 상세 보기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = { showExitDialog = true }) { //  X 버튼 추가
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (videoDetail == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val detail = videoDetail!!


            Column(modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()) {

                YouTubePlayerComponent(
                    videoUrl = detail.videoUrl,
                    seekToMillis = seekToMillis,
                    playbackSpeed = playbackSpeed
                )

                Spacer(Modifier.height(12.dp))

                Row(Modifier.padding(horizontal = 16.dp)) {
                    Button(onClick = { subtitleMode = "BOTH" }, modifier = Modifier.padding(end = 8.dp)) {
                        Text("영문+한글")
                    }
                    Button(onClick = { subtitleMode = "EN_ONLY" }) {
                        Text("영문만")
                    }
                }

                Spacer(Modifier.height(8.dp))


                Text("자막", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))


                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)) {
                    itemsIndexed(detail.sentences) { index, segment ->
                        val displayText = when (subtitleMode) {
                            "EN_ONLY" -> segment.originalText
                            "BOTH" -> "${segment.originalText}\n${segment.translatedText}"
                            else -> segment.originalText
                        }

                        val isHighlighted = segment.startTimeMillis == highlightedMillis

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isHighlighted) Color(0xFFE3F2FD) else Color.Transparent)
                                .padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        seekToMillis = segment.startTimeMillis.toFloat() + 500
                                        highlightedMillis = segment.startTimeMillis
                                    }
                            ) {
                                when (subtitleMode) {
                                    "EN_ONLY" -> {
                                        ClickableWordText(sentence = segment.originalText) { word ->
                                            selectedWord = word
                                            viewModel.loadWordDetail(word)
                                            showWordDialog = true
                                        }
                                    }
                                    "BOTH" -> {
                                        ClickableWordText(sentence = segment.originalText) { word ->
                                            selectedWord = word
                                            viewModel.loadWordDetail(word)
                                            showWordDialog = true
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(text = segment.translatedText)
                                    }
                                    else -> {
                                        Text(text = segment.originalText)
                                    }
                                }
                            }
                            val scope = rememberCoroutineScope()

                            // 북마크 버튼 추가: 이 문장이 몇 번째인지 기반으로 진도율 계산
                            IconButton(onClick = {
                                val progress = ((index + 1).toFloat() / detail.sentences.size * 100).roundToInt()

                                scope.launch {
                                    viewModel.getLibraryId(userId, contentsType, contentId) { libraryId ->
                                        if (libraryId != null) {
                                            viewModel.updateProgress(libraryId, progress.toFloat())
                                        } else {
                                            Toast.makeText(context, "라이브러리 ID 불러오기 실패", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }) {
                                Icon(Icons.Default.Bookmark, contentDescription = "북마크")
                            }
                        }
                    }
                }
            }
            FloatingBadge(
                navController = navController,
                contentType = contentsType,
                contentId = contentId
            )
        }
    }
    // 팝업 UI
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("학습을 종료하시겠습니까?") },
            text = { Text("학습을 종료하고 유사한 콘텐츠를 추천받거나 홈 화면으로 돌아갈 수 있습니다.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false

                    navController.navigate("similar_content/$contentsType/$contentId")
                }) {
                    Text("유사한 콘텐츠 학습하기")
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        showExitDialog = false
                        navController.navigate("home")
                    }) {
                        Text("홈으로")
                    }
                    TextButton(onClick = {
                        showExitDialog = false
                    }) {
                        Text("취소")
                    }
                }
            }
        )
    }

    if (showWordDialog && selectedWord.isNotBlank()) {
        WordDetailDialog(
            word = selectedWord,
            onClose = { showWordDialog = false },
            onFavorite = {
                //val uid = Firebase.auth.currentUser?.uid ?: return@WordDetailDialog  // 👉 이건 위에서 받아오도록 처리 필요 (아래 설명 참고)
                viewModel.addWordToUserVocab(selectedWord, userId)
            },
            wordInfo = viewModel.selectedWordInfo.collectAsState().value
        )
    }
}

@Composable
fun YouTubePlayerComponent(
    videoUrl: String,
    seekToMillis: Float?,
    playbackSpeed: Float
) {
    AndroidView(factory = { context ->
        val webView = WebView(context).apply {
            settings.javaScriptEnabled = true
            val videoId = Uri.parse(videoUrl).getQueryParameter("v") ?: ""

            val html = """
                <html>
                <body style="margin:0;">
                <div id="player"></div>
                <script>
                  var tag = document.createElement('script');
                  tag.src = "https://www.youtube.com/iframe_api";
                  var firstScriptTag = document.getElementsByTagName('script')[0];
                  firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                  var player;
                  function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                      height: '360',
                      width: '100%',
                      videoId: '$videoId',
                      playerVars: {
                        autoplay: 1,
                        controls: 1,
                        rel: 0
                      },
                      events: {
                        onReady: onPlayerReady
                      }
                    });
                  }

                  function onPlayerReady(event) {
                    event.target.setPlaybackRate($playbackSpeed);
                  }

                  function seekToPosition(millis) {
                    if (player && millis > 0) {
                      player.seekTo(millis / 1000, true);
                    }
                  }

                  window.seekTo = function(ms) {
                    seekToPosition(ms);
                  }
                </script>
                </body>
                </html>
            """
            loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
        }
        webView
    }, update = { webView ->
        seekToMillis?.let {
            webView.evaluateJavascript("seekTo($it);", null)
        }
    })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClickableWordText(
    sentence: String,
    onWordClick: (String) -> Unit
) {
    val words = sentence.split(Regex("\\s+"))
    FlowRow(modifier = Modifier.fillMaxWidth()) {
        words.forEach { word ->
            val cleanWord = word.trim().filter { it.isLetterOrDigit() }
            Text(
                text = "$word ",
                modifier = Modifier
                    .clickable { onWordClick(cleanWord) }
                    .padding(horizontal = 2.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}



