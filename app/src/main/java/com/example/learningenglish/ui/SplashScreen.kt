package com.example.learningenglish.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.learningenglish.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // 로딩 완료 여부를 기억하는 상태
    var isLoadingComplete by remember { mutableStateOf(false) }

    // 2초 후 로딩 완료 처리 (딜레이)
    LaunchedEffect(Unit) {
        delay(2000) // 2초 대기
        isLoadingComplete = true
    }

    // 로딩 완료되면 LoginScreen으로 이동
    LaunchedEffect(isLoadingComplete) {
        if (isLoadingComplete) {
            navController.navigate("login") {
                popUpTo("splash") { inclusive = true } // 스택에서 splash 제거
            }
        }
    }

    // 로딩 화면 UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp)) // 상단 여백

        // 메인 로고
        Image(
            painter = painterResource(id = R.drawable.ic_mainlogo), // 앱 로고 파일
            contentDescription = "앱 로고",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(80.dp))

        // 하단 로딩 인디케이터
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color(0xFF3F51B5)
        )
    }
}
