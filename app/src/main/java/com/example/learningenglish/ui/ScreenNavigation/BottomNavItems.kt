package com.example.learningenglish.ui.ScreenNavigation

import androidx.compose.runtime.Composable
import com.example.learningenglish.R

//@Composable
sealed class BottomNavItem(val route: String, val icon: Int, val label: String) {
    object Home : BottomNavItem("home", R.drawable.ic_learn, "홈화면")
    object Library : BottomNavItem("library", R.drawable.ic_grade, "라이브러리")
    object MyPage : BottomNavItem("mypage", R.drawable.ic_vocab, "프로필")
}
