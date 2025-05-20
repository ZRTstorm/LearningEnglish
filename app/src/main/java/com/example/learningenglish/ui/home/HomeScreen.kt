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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.learningenglish.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    var showGoalDialog by remember { mutableStateOf(false) }
    var showLearningTypeDialog by remember { mutableStateOf(false) }
    var selectedLearningType by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf(0) }
    var goalMinutes by remember { mutableStateOf(0) }
    var elapsedTimeInMinutes by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

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

    Scaffold(
        containerColor = Color(0xFFF0F4FF),
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
            BottomNavigationBar(navController = navController, items = items)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
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
                    ProfileSection()
                }
            }
            TodayLearningProgress(
                goalHours = goalHours,
                goalMinutes = goalMinutes,
                elapsedTimeInMinutes = elapsedTimeInMinutes
            ) // 예: 60% 완료

            // 학습하기 버튼
            StartLearningButton {
                showGoalDialog = true
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 학습 현황 섹션
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    LearningStatusSection()
                }
            }

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

            // 학습유형 선택 Dialog
            if (showLearningTypeDialog) {
                LearningTypeSelectionDialog(
                    onConfirm = { type ->
                        selectedLearningType = type
                        navController.navigate("learningstart/${goalHours}/${goalMinutes}/${type}")
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

@Composable
fun ProfileSection() {
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
            text = "홍길동님",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun StartLearningButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text("학습하기", style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
fun TodayLearningProgress(goalHours: Int, goalMinutes: Int, elapsedTimeInMinutes: Int) {
    // 목표 시간을 분 단위로 변환
    val totalGoalMinutes = goalHours * 60 + goalMinutes

    // 경과된 시간과 목표 시간을 비교하여 진행률 계산
    val progress = (elapsedTimeInMinutes.toFloat() / totalGoalMinutes) // 0.0f ~ 1.0f 사이의 값

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "오늘 학습 진행률",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = Color(0xFF3F51B5),
            trackColor = Color(0xFFE0E0E0)
        )
        Spacer(modifier = Modifier.height(4.dp))
        // 진행률을 퍼센트로 표시
        val percentage = (progress * 100).toInt()
        Text(text = "$percentage% 완료")
    }
}







