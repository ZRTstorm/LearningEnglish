package com.example.learningenglish.ui.ScreenNavigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.repository.WordRepository
import com.example.learningenglish.ui.auth.AttendancePreferencesDataStore
import com.example.learningenglish.ui.auth.AuthManager
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.ui.home.HomeScreen
import com.example.learningenglish.ui.mypage.MyPageScreen
import com.example.learningenglish.ui.recommendation.LibraryScreen
import com.example.learningenglish.viewmodel.AuthViewModel
import com.example.learningenglish.viewmodel.AuthViewModelFactory
import com.example.learningenglish.viewmodel.LearningViewModel
import com.example.learningenglish.viewmodel.LearningViewModelFactory


@Composable
fun BottomBarScreen(navController: NavHostController) {
    // ViewModelStore 설정
    val navHostController = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Library,
        BottomNavItem.MyPage
    )

    val context = LocalContext.current
    val userPrefs = remember {UserPreferencesDataStore(context)}
    val attendancePrefs = remember { AttendancePreferencesDataStore(context) }

    val repository = LearningRepository()
    val repositoryW = WordRepository(context, userPrefs)

    val viewModel: LearningViewModel = viewModel(
        factory = LearningViewModelFactory(
            LearningRepository(),
            WordRepository(context, userPrefs), // 필요한 context와 prefs 포함
            attendancePrefs
        )
    )

    Scaffold(
        bottomBar = {
            // 하단 네비게이션 바
            BottomNavigationBar(navController = navHostController, items = items)
        }
    ) { innerPadding ->
        // Box로 화면을 감싸고 innerPadding을 적용하여 하단 바와 겹치지 않도록
        Box(modifier = Modifier.padding(innerPadding)) {
            // NavHost 설정
            NavHost(
                navController = navHostController,
                startDestination = "home"  // "home"을 첫 번째 화면으로 설정
            ) {
                composable("home") {
                    val context = LocalContext.current
                    val authManager = remember { AuthManager(context) }
                    val userPrefs = remember { UserPreferencesDataStore(context) }
                    val AuthviewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authManager, userPrefs))

                    HomeScreen(
                        navController = navController,
                        viewModel = AuthviewModel,
                        learningViewModel = viewModel
                    )
                }
                composable("library") {
                    val viewModel: LearningViewModel = viewModel()  // viewModel 생성
                    LibraryScreen(navController = navHostController, viewModel = viewModel)
                }
                composable("mypage") {
                    MyPageScreen(navController = navHostController)
                }
            }
        }
    }
}
