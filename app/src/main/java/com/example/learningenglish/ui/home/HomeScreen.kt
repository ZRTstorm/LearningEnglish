package com.example.learningenglish.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.learningenglish.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import com.example.learningenglish.ui.ScreenNavigation.BottomNavItem
import com.example.learningenglish.ui.ScreenNavigation.BottomNavigationBar
import com.example.learningenglish.ui.mypage.MyPageScreen
import com.example.learningenglish.ui.recommendation.LibraryScreen
import com.example.learningenglish.viewmodel.AuthViewModel
import com.example.learningenglish.viewmodel.LearningViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch


@Composable
fun AttendanceBadge(days: Int) {
    Box(
        modifier = Modifier
            .background(color = Color(0xFFFFE0E0), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = "${days}일째 출석 중",
            color = Color(0xFFD32F2F),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileSection(userName: String, greetingMessage: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.ic_profile_boy),
            contentDescription = "프로필",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Gray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "$greetingMessage ${userName}님",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun StartLearningButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("학습하기", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = null
            )
        }
    }
}

@Composable
fun SwipableLearningCards(
    recentTitle: String?,
    recentType: String?,
    recentContentId: Int?,
    userId: Int,
    todayWord: String?,
    navController: NavController
) {
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(count = 2, state = pagerState) { page ->
            when (page) {
                0 -> LearningCard(
                    title = "최근 학습",
                    subtitle = recentTitle ?: "내역 없음",
                    imageRes = R.drawable.recent_learning,
                    buttonText = "이어 하기",
                    onClick = {
                        if (recentContentId != null && recentType != null) {
                            when (recentType) {
                                "video" -> navController.navigate("videodetail/video/$recentContentId")
                                "text" -> navController.navigate("textdetail/text/$recentContentId")
                                else -> Toast.makeText(context, "지원하지 않는 콘텐츠 유형입니다", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "최근 학습 정보가 없습니다", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                1 -> LearningCard(
                    title = "오늘의 단어",
                    subtitle = todayWord ?: "등록된 단어 없음",
                    imageRes = R.drawable.todays_word,
                    buttonText = "학습 하기",
                    onClick = {
                        navController.navigate("uservocab/${userId}")
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = Color(0xFF7C4DFF),
            inactiveColor = Color.LightGray
        )
    }
}

@Composable
fun LearningCard(
    title: String,
    subtitle: String,
    imageRes: Int,
    buttonText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 상단 제목/서브타이틀
                Column (
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.6f), shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.Black,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = subtitle,
                        color = Color.DarkGray,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // 버튼 하단 정렬

                // 하단 버튼
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(buttonText, color = Color.Black, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_right),
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }
        }
    }
}




    @Composable
    fun AttendanceCalendar(
        todayChecked: Boolean,
        onCheckIn: () -> Unit,
        checkedDates: List<LocalDate>
    ) {
        val formatter = DateTimeFormatter.ofPattern("MM/dd")
        val today = LocalDate.now()

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("출석 체크", style = MaterialTheme.typography.titleLarge)
                if (!todayChecked) {
                    Button(onClick = onCheckIn) {
                        Text("출석하기")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 간단한 출석 달력 (7일 고정)
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(7) { index ->
                    val date = today.minusDays((6 - index).toLong())
                    val isChecked = checkedDates.contains(date)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (isChecked) Color(0xFF81C784) else Color.LightGray,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = date.format(formatter).substring(3))
                    }
                }
            }

            if (todayChecked) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "오늘 출석 완료!",
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    userId: Int,
    learningViewModel: LearningViewModel
) {

    var showLearningTypeDialog by remember { mutableStateOf(false) }
    var selectedLearningType by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val userName by viewModel.userName.collectAsState()
    val todayChecked by learningViewModel.todayChecked.collectAsState()
    val checkedDates by learningViewModel.checkedDates.collectAsState()
    val consecutiveDays by learningViewModel.consecutiveDays.collectAsState()

    val purpleColor = Color(0xFF7C4DFF)
    val lightPurple = Color(0xFFF6F3FF)
    val darkPurple = Color(0xFF4A3AFF)

    var recentTitle by remember { mutableStateOf<String?>(null) }
    var recentType by remember { mutableStateOf<String?>(null) }
    var recentContentId by remember { mutableStateOf<Int?>(null) }

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Library,
        BottomNavItem.MyPage
    )

    val snackbarHostState = remember{ SnackbarHostState()}
    val coroutineScope = rememberCoroutineScope()

    val encouragementMessages = listOf(
        "오늘도 힘내요! 🚀",
        "멋진 시작입니다! ✨",
        "한 걸음씩 나아가요! 🏃",
        "성공적인 학습을 응원합니다! 📚",
        "최고예요! 계속 힘내요! 💪"
    )

    var greetingMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    val vocabList by learningViewModel.pagedUserVocab.collectAsState()
    var page by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.loadUserDisplayName()
        greetingMessage = encouragementMessages.random()
        learningViewModel.initializeAttendance()
    }

    LaunchedEffect(Unit) {
        learningViewModel.initRepository(context)
          // 꼭 필요
    }

    LaunchedEffect(userId) {
        learningViewModel.loadRecentLearning(userId) { title, type, id ->
            recentTitle = title
            recentType = type
            recentContentId = id
        }
    }

    LaunchedEffect(userId) {
        learningViewModel.loadUserVocab(userId) // 직접 호출 필요
    }

    Scaffold(
        containerColor = lightPurple,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo),
                            contentDescription = "앱 로고",
                            modifier = Modifier
                                .height(32.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "LearningApp",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.Black
                        )

                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack, // 기본 왼쪽 화살표 아이콘
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "로그아웃",
                            tint = Color.Black
                        )
                    }
                },
                modifier = Modifier.height(56.dp),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("로그아웃") },
                    text = { Text("로그아웃 하시겠습니까?") },
                    confirmButton = {
                        TextButton(onClick = {
                            showDialog = false
                            viewModel.logout()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        }) {
                            Text("예")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("아니오")
                        }
                    }
                )
            }
        },
        bottomBar = {
            val currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
            CustomBottomBar(navController, currentRoute)
        }
    ) { innerPadding ->
        val today = LocalDate.now()
        val seed = today.toEpochDay() + if (userId > 0) userId else 12345
        val todayWord = remember(vocabList) {
            derivedStateOf {
                if (page == 0 && vocabList.isNotEmpty()) {
                    val random = java.util.Random(seed)
                    vocabList[random.nextInt(vocabList.size)].word
                } else null
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFF6F3FF), Color.White),
                        startY = 0f,
                        endY = 1000f
                    )
                )
                .padding(innerPadding)
                .padding(16.dp)
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            AttendanceBadge(days = consecutiveDays) // 임시값
            // 프로필 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {

                    ProfileSection(userName = userName, greetingMessage = greetingMessage)
                }
            }

            // 학습하기 버튼
            StartLearningButton {
                navController.navigate("datalearningstart")
            }


            Spacer(modifier = Modifier.height(24.dp))

            SwipableLearningCards(
                recentTitle = recentTitle,
                recentType = recentType,
                recentContentId = recentContentId,
                userId = userId,
                todayWord = todayWord.value,
                navController = navController,
            )


            // 출석 달력 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    AttendanceCalendar(
                        todayChecked = todayChecked,
                        onCheckIn = { learningViewModel.checkAttendance() },
                        checkedDates = checkedDates
                    )
                }
            }


            // 학습유형 선택 Dialog
            if (showLearningTypeDialog) {
                LearningTypeSelectionDialog(
                    onConfirm = { type ->
                        selectedLearningType = type
                        navController.navigate("uploadtypeselect")
                        showLearningTypeDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = encouragementMessages.random(),
                                actionLabel = "확인"
                            )
                        }
                    },
                    onDismiss = { showLearningTypeDialog = false }
                )
            }
        }

    }
}











