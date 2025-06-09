package com.example.learningenglish.ui.home

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

    @Composable
    fun TodayRecommendationCard(onClick: () -> Unit) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("오늘의 추천", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("3문장 일상 영어 회화", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Button(onClick = onClick) {
                    Text("바로가기")
                }
            }
        }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)  // 위치 조절
                    .background(Color(0xFFD7CCC8), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Button(onClick = onClick) {
                    Text("바로가기",style = MaterialTheme.typography.labelSmall)
                }
            }
    }
}

    @Composable
    fun LearningStatsCard(
        studyCount: Int = 4,
        totalMinutes: Int = 85,
        averageScore: Int = 87
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("나의 학습 현황", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("📅 이번 주 학습 ${studyCount}회")
                val hours = totalMinutes / 60
                val minutes = totalMinutes % 60
                Text("⏱️ 누적 시간 ${hours}시간 ${minutes}분")
                Text("📈 평균 점수 ${averageScore}점")
            }
        }
    }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel,
    learningViewModel: LearningViewModel
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showLearningTypeDialog by remember { mutableStateOf(false) }
    var selectedLearningType by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf(0) }
    var goalMinutes by remember { mutableStateOf(0) }
    var elapsedTimeInMinutes by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    val userName by viewModel.userName.collectAsState()
    val todayChecked by learningViewModel.todayChecked.collectAsState()
    val checkedDates by learningViewModel.checkedDates.collectAsState()
    val consecutiveDays by learningViewModel.consecutiveDays.collectAsState()

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

    LaunchedEffect(Unit) {
        viewModel.loadUserDisplayName()
        greetingMessage = encouragementMessages.random()
    }

    Scaffold(
        containerColor = Color(0xFFFFFAF0),
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
                            painter = painterResource(id = R.drawable.ic_logout), // 로그아웃 아이콘 (예: 문 출구 모양)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                showLearningTypeDialog = true
            }

            Spacer(modifier = Modifier.height(24.dp))

            TodayRecommendationCard(onClick = {
                navController.navigate("library")
            })

            LearningStatsCard()

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

            /*
            // 목표 설정 Dialog
            if (showGoalDialog) {
                GoalSettingDialog(
                    onConfirm = { hours, minutes ->
                        goalHours = hours
                        goalMinutes = minutes
                        showGoalDialog = false
                        showLearningTypeDialog = true
                    },
                    onDismiss = { showGoalDialog = false }
                )
            }
             */


            // 학습유형 선택 Dialog
            if (showLearningTypeDialog) {
                LearningTypeSelectionDialog(
                    onConfirm = { type ->
                        selectedLearningType = type
                        navController.navigate("datalearningstart")
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











