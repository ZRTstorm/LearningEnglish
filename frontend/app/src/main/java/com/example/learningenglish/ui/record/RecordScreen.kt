package com.example.learningenglish.ui.record

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordScreen(navController: NavController) {
    val userUid = FirebaseAuth.getInstance().currentUser?.uid

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("기록") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("@분째 학습 중", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("유사 콘텐츠 추천", color = Color.Gray, fontSize = 14.sp)
                            Text("바로가기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("계정 정보", color = Color.Gray, fontSize = 14.sp)
                            Text("수정하기", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            RecordItem(title = "📘 나의 단어장", subtitle = "저장한 단어를 복습해보세요") {
                navController.navigate("uservocab/$userUid")
            }

            RecordItem(title = "🎤 퀴즈 기록", subtitle = "최근 퀴즈 내역을 확인하세요") {
                navController.navigate("quiz_history")
            }

            RecordItem(title = "📅 출석 달력", subtitle = "나의 연속 학습 일수를 확인하세요") {
                navController.navigate("attendance_calendar")
            }
        }
    }
}

@Composable
fun RecordItem(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .padding(bottom = 12.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = Color.Gray, fontSize = 13.sp)
        }
        Icon(Icons.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray)
    }
    Spacer(modifier = Modifier.height(8.dp))
}
