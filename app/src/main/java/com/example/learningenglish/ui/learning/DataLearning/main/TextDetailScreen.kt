package com.example.learningenglish.ui.learning.DataLearning.main

import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import com.example.learningenglish.ui.learning.DataLearning.main.ClickableWordText
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.learningenglish.data.remote.RetrofitInstance.api
import com.example.learningenglish.data.util.saveDownloadedFile
import com.example.learningenglish.ui.Word.WordDetailDialog
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.viewmodel.LearningViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.material.icons.filled.Bookmark


@Composable
fun Mp3PlayerComponent(
    file: File,
    seekToMillis: Int? = null
) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    val scope = rememberCoroutineScope()


    LaunchedEffect(file.absolutePath) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, file.toUri())  // File → Uri로 처리
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: Exception) {
            Log.e("Mp3Player", "재생 실패: ${e.localizedMessage}")
        }
    }

    LaunchedEffect(seekToMillis) {
        seekToMillis?.let {
            mediaPlayer.seekTo(it)
            if (!mediaPlayer.isPlaying) mediaPlayer.start()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

@Composable
fun MusicPlayerComponent(
    file: File,
    isPlaying: Boolean,
    seekToMillis: Float?,
    onPlayPauseToggle: () -> Unit
) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }

    // 전체 음성 준비 및 플레이
    LaunchedEffect(file.absolutePath) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, file.toUri())
            mediaPlayer.prepare()
            //if (isPlaying) mediaPlayer.start()
        } catch (e: Exception) {
            Log.e("MediaPlayer", "파일 로딩 실패: ${e.localizedMessage}")
        }
    }

    // 전체 재생 / 일시정지 토글
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            if (!mediaPlayer.isPlaying) mediaPlayer.start()
        } else {
            if (mediaPlayer.isPlaying) mediaPlayer.pause()
        }
    }

    // 특정 문장 클릭 시 재생 위치 이동
    LaunchedEffect(seekToMillis) {
        seekToMillis?.let {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.seekTo(it.toInt())
            } else {
                mediaPlayer.seekTo(it.toInt())
                mediaPlayer.start()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    // 🎧 UI
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        /*
        IconButton(onClick = onPlayPauseToggle) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "재생/일시정지"
            )
        }
        Text(if (isPlaying) "재생 중..." else "일시 정지됨", style = MaterialTheme.typography.bodyMedium)

         */
    }
}

fun getGroupedContentIds(baseId: Int): Map<String, Int> {
    val mod = baseId % 3
    return when (mod) {
        1 -> mapOf("EN" to baseId, "GB" to baseId + 1, "AU" to baseId + 2)
        2 -> mapOf("EN" to baseId - 1, "GB" to baseId, "AU" to baseId + 1)
        0 -> mapOf("EN" to baseId - 2, "GB" to baseId - 1, "AU" to baseId)
        else -> emptyMap()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextDetailScreen(
    viewModel: LearningViewModel,
    navController: NavController,
    contentsType: String,
    contentId: Int
) {
    val textDetail by viewModel.textDetail.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferencesDataStore(context) }
    val userIdState = remember { mutableStateOf(0) }
    val userId = userIdState.value

    LaunchedEffect(Unit) {
        viewModel.initRepository(context) // 무조건 가장 먼저 실행되도록
        userIdState.value = userPrefs.getUserId().first() ?: 0
        viewModel.loadUserVocab(userIdState.value)
    }

    var subtitleMode by remember { mutableStateOf("BOTH") }
    var playbackSpeed by remember { mutableStateOf(1.0f) }
    var seekToMillis by remember { mutableStateOf<Float?>(null) }
    var highlightedMillis by remember { mutableStateOf<Long?>(null) }



    var selectedWord by remember { mutableStateOf("") }
    val wordInfo by viewModel.selectedWordInfo.collectAsState()
    var showWordDialog by remember { mutableStateOf(false) }

    val downloadedFiles = remember { mutableStateMapOf<String, File>() }
    var selectedVersion by remember { mutableStateOf("EN") }

    var isPlaying by remember { mutableStateOf(false) }
    val mediaPlayer = remember { MediaPlayer() }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    var savedWords = remember { mutableStateListOf<String>() }
    //val savedWords = viewModel.userVocab.collectAsState().value.map { it.word }

    val selectedVersionColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = Color.White
    )

    val unselectedVersionColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary
    )

    var showExitDialog by remember { mutableStateOf(false) }


    val difficultyRange by remember { mutableStateOf(1f..15f) }


    LaunchedEffect(userId) {
        viewModel.loadUserVocab(userId)
    }
    LaunchedEffect(contentId) {
        viewModel.loadTextDetail(contentId)

        val groupedIds = getGroupedContentIds(contentId)
        groupedIds.forEach { (version, realId) ->
            coroutineScope.launch {
                val response = api.downloadFile("text", realId)
                val file = saveDownloadedFile(
                    response,
                    "text_${realId}_$version.mp3",
                    context
                )
                if (file != null) {
                    downloadedFiles[version] = file
                    Log.d("Download", "$version 파일 저장 성공: ${file.absolutePath}")
                } else {
                    Log.e("Download", "$version 파일 저장 실패")
                }
            }
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("음성 상세 듣기") },
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
        Column(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            Spacer(Modifier.height(12.dp))

            //  음성 버전 선택 버튼
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("EN", "GB", "AU").forEach { version ->
                    Button(
                        onClick = { selectedVersion = version },
                        colors = if (selectedVersion == version) selectedVersionColors else unselectedVersionColors,
                        border = if (selectedVersion != version)
                            ButtonDefaults.outlinedButtonBorder
                        else null
                    ) {
                        Text(version)
                    }
                }
            }

            //  코드 (2) — 음성 재생 및 자막 표시, 단어 클릭 처리 포함

            Spacer(Modifier.height(12.dp))

            //  선택된 음성 파일 재생
            val selectedFile = downloadedFiles[selectedVersion]
            if (selectedFile != null) {
                Mp3PlayerComponent(file = selectedFile, seekToMillis = seekToMillis?.toInt())
            } else {
                Text("음성 파일을 불러오는 중입니다...", modifier = Modifier.padding(16.dp))
            }

            Spacer(Modifier.height(12.dp))

            // 자막 보기 설정 버튼
            Row(Modifier.padding(horizontal = 16.dp)) {
                Button(onClick = { subtitleMode = "BOTH" }, modifier = Modifier.padding(end = 8.dp)) {
                    Text("영문+한글")
                }
                Button(onClick = { subtitleMode = "EN_ONLY" }) {
                    Text("영문만")
                }
            }

            selectedFile?.let { file ->
                MusicPlayerComponent(
                    file = file,
                    isPlaying = isPlaying,
                    seekToMillis = seekToMillis,
                    onPlayPauseToggle = { isPlaying = !isPlaying }
                )
            }

            Spacer(Modifier.height(12.dp))

            //  자막 리스트
            val sentences = textDetail?.textFiles?.firstOrNull()?.sentences ?: emptyList()
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)) {
                items(sentences.size) { index ->
                    val segment = sentences[index]
                    val isHighlighted = segment.startTimeMillis == highlightedMillis
                    val scope = rememberCoroutineScope()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
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
                                else -> Text(text = segment.originalText)
                            }
                        }
                        IconButton(onClick = {
                            scope.launch {
                                viewModel.getLibraryId(userId, contentsType, contentId) { libraryId ->
                                    if (libraryId != null) {
                                        val progress = ((index + 1).toFloat() / sentences.size * 100).toInt()
                                        viewModel.updateProgress(libraryId, progress.toFloat())
                                    } else {
                                        Toast.makeText(context, "라이브러리 ID 불러오기 실패", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                textDetail?.let {
                                    viewModel.saveRecentLearning(
                                        userId = userId,
                                        title = it.title,
                                        contentType = contentsType,
                                        contentId = contentId
                                    )
                                }
                            }
                        }) {
                            Icon(Icons.Default.Bookmark, contentDescription = "북마크")
                        }
                    }
                }
            }


            // 팝업 UI
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = { Text("학습을 종료하시겠습니까?") },
                    text = { Text("학습을 종료하고 홈 화면으로 돌아갈 수 있습니다.") },
                    confirmButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                            navController.navigate("home")
                        }) {
                            Text("홈으로")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showExitDialog = false
                        }) {
                            Text("취소")
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
                        if (!savedWords.contains(selectedWord)) {
                            viewModel.addWordToUserVocab(selectedWord, userId)
                            savedWords.add(selectedWord)  // ✅ 저장된 단어로 기억
                            Toast.makeText(context, "\"$selectedWord\" 등록 완료!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "이미 등록된 단어입니다.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    wordInfo = viewModel.selectedWordInfo.collectAsState().value
                )
            }
        }
        FloatingBadge(
            navController = navController,
            contentType = contentsType,
            contentId = contentId
        )
    }
}

@Composable
fun FloatingBadge(
    navController: NavController,
    contentType: String,
    contentId: Int
) {
    var badgeVisible by remember { mutableStateOf(true) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(horizontalAlignment = Alignment.End) {
            // 숨겨진 상태일 땐 ▸ 버튼만 보임
            if (!badgeVisible) {
                IconButton(onClick = { badgeVisible = true }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "다시 보기")
                }
            } // 애니메이션 포함된 배지 UI (보일 때만)
            AnimatedVisibility(
                visible = badgeVisible,
                enter = fadeIn() + expandIn(),
                exit = fadeOut() + shrinkOut()
            ) {
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = Color(0xFF673AB7),
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier.clickable {
                            showConfirmDialog = true
                        }
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("유사한 콘텐츠", color = Color.White)
                            Text("학습하기", color = Color.White)
                        }
                    }

                    TextButton(onClick = { badgeVisible = false }) {
                        Text("숨기기 ▸", color = Color.Gray)
                    }
                }
            }
        }

    // 예 / 아니오 다이얼로그
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("이동하시겠습니까?") },
                confirmButton = {
                    TextButton(onClick = {
                        showConfirmDialog = false
                        navController.navigate("similar_content/$contentType/$contentId")
                    }) {
                        Text("예")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("아니오")
                    }
                }
            )
        }

    }
}










