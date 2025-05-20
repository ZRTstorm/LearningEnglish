package com.example.learningenglish.ui.learning.DataLearning.main

import android.net.Uri
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.learningenglish.ui.Word.WordDetailDialog
import com.example.learningenglish.viewmodel.LearningViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    viewModel: LearningViewModel,
    navController: NavController,
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




    LaunchedEffect(contentId) {
        viewModel.loadVideoDetail(contentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("영상 상세 보기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
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

                Row(Modifier.padding(horizontal = 16.dp)) {
                    listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { speed ->
                        Button(
                            onClick = { playbackSpeed = speed },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (playbackSpeed == speed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text("${speed}x")
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text("자막", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))

                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)) {
                    items(detail.sentences.size) { index ->
                        val segment = detail.sentences[index]
                        val displayText = when (subtitleMode) {
                            "EN_ONLY" -> segment.originalText
                            "BOTH" -> "${segment.originalText}\n${segment.translatedText}"
                            else -> segment.originalText
                        }

                        val isHighlighted = segment.startTimeMillis == highlightedMillis

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isHighlighted) Color(0xFFE3F2FD) else Color.Transparent)
                                .clickable {
                                    seekToMillis = segment.startTimeMillis.toFloat() + 500
                                    highlightedMillis = segment.startTimeMillis
                                }
                                .padding(vertical = 8.dp)
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

                    }
                }
            }
        }
    }

    if (showWordDialog && selectedWord.isNotBlank()) {
        WordDetailDialog(
            word = selectedWord,
            onClose = { showWordDialog = false },
            onFavorite = {
                val uid = Firebase.auth.currentUser?.uid ?: return@WordDetailDialog  // 👉 이건 위에서 받아오도록 처리 필요 (아래 설명 참고)
                viewModel.addWordToUserVocab(selectedWord, uid)
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


/*
ClickableWordText(sentence.originalText) { selectedWord ->
    selectedWordState.value = selectedWord
    showWordDialog.value = true
}
*/

