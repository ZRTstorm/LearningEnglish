package com.example.learningenglish

import android.app.Activity
import android.content.Intent
import android.os.Bundle
  import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.learningenglish.data.model.ContentType
import com.example.learningenglish.data.model.LearningResponse
import com.example.learningenglish.data.model.TextMapping
import com.example.learningenglish.data.model.TimingInfo
import com.example.learningenglish.data.model.WordInfo
import com.example.learningenglish.data.repository.LearningRepository
import com.example.learningenglish.data.util.Resource
import com.example.learningenglish.ui.ScreenNavigation.BottomBarScreen
import com.example.learningenglish.ui.SplashScreen
import com.example.learningenglish.ui.auth.AuthManager
import com.example.learningenglish.ui.auth.LoginScreen
import com.example.learningenglish.ui.auth.SignUpScreen
import com.example.learningenglish.ui.learning.dictation.DictationScreen
import com.example.learningenglish.ui.grade.GradeTestHomeScreen
import com.example.learningenglish.ui.home.FeatureSelectionScreen
import com.example.learningenglish.ui.home.HomeScreen
import com.example.learningenglish.ui.learning.DataLearning.DataLearningStartScreen
import com.example.learningenglish.ui.learning.DataLearning.main.DataLearningMainScreen
import com.example.learningenglish.ui.learning.DataLearning.main.VideoLearningMainScreen
//import com.example.learningenglish.ui.learning.DataLearning.LearningStartScreen
import com.example.learningenglish.ui.learning.DataLearning.upload.ImageUploadScreen
import com.example.learningenglish.ui.learning.DataLearning.upload.UploadResultScreen
import com.example.learningenglish.ui.learning.DataLearning.upload.UploadTypeSelectScreen
import com.example.learningenglish.ui.learning.DataLearning.upload.VideoLinkUploadScreen
import com.example.learningenglish.ui.learning.LearningStartScreen
import com.example.learningenglish.ui.mypage.EditProfileScreen
import com.example.learningenglish.ui.mypage.MyPageScreen
import com.example.learningenglish.ui.learning.pronunciation.PronunciationTestScreen
import com.example.learningenglish.ui.recommendation.RecommendationScreen
import com.example.learningenglish.ui.learning.summary.SummaryTestScreen
import com.example.learningenglish.ui.theme.LearningEnglishTheme
import com.example.learningenglish.ui.learning.vocabulary.VocabScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.learningenglish.data.repository.WordRepository
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.ui.learning.DataLearning.main.VideoDetailScreen
import com.example.learningenglish.ui.learning.DataLearning.upload.OcrResultScreen
import com.example.learningenglish.ui.recommendation.AllVideoLibraryScreen
import com.example.learningenglish.ui.recommendation.LibraryScreen
import com.example.learningenglish.viewmodel.AuthViewModel
import com.example.learningenglish.viewmodel.AuthViewModelFactory
import com.example.learningenglish.viewmodel.LearningViewModel
import com.example.learningenglish.viewmodel.LearningViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val RC_SIGN_IN = 9001  // 구글 로그인 결과 코드
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        val authManager = AuthManager(this)
        val signInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    lifecycleScope.launch {
                        val user = authManager.firebaseAuthWithGoogle(task.result)
                        user?.let {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }



        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            LearningEnglishTheme {
                // NavController를 기억하고, 이를 LoginScreen에 전달

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                //val learningViewModel: LearningViewModel = viewModel()
                val coroutineScope = rememberCoroutineScope()
                val authManager = AuthManager(this)

                val navHostController = navController as NavHostController
                navHostController.setViewModelStore(this.viewModelStore) // ViewModelStore 설정

                val context = LocalContext.current
                val userPrefs = remember {UserPreferencesDataStore(context)}

                val repository = LearningRepository()
                val repositoryW = WordRepository(context, userPrefs)

                // NavHost로 네비게이션을 처리ㄷ
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("splash") {
                        SplashScreen(navController)
                    }
                    composable("login") {
                        LoginScreen(
                            navController = navController,
                            onLoginClick = { email, password ->
                                coroutineScope.launch {
                                    val user = authManager.signInWithEmail(email, password)
                                    if (user != null) {
                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                }
                            },
                            onGoogleLoginClick = {
                                authManager.signInWithGoogle(this@MainActivity, signInLauncher)
                            },
                            onSignUpClick = { navController.navigate("signup") },
                            onForgotPasswordClick = {}
                        )
                    }

                    composable("signUp") {
                        SignUpScreen(
                            onSignUpClick = { email, password, _, nickname ->
                                coroutineScope.launch {
                                    authManager.signUpWithEmail(email, password, nickname)
                                    navController.navigate("login") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                    composable("home") {
                        val context = LocalContext.current
                        val authManager = remember { AuthManager(context) }
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authManager, userPrefs))

                        HomeScreen(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }

                    composable("featureselection") {
                        FeatureSelectionScreen(navController = navController)
                    }
                    composable(
                        route = "learningstart/{goalHours}/{goalMinutes}/{selectedLearningType}",
                        arguments = listOf(
                            navArgument("goalHours") { type = NavType.IntType },
                            navArgument("goalMinutes") { type = NavType.IntType },
                            navArgument("selectedLearningType") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0
                        val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 0
                        val selectedLearningType = backStackEntry.arguments?.getString("selectedLearningType") ?: "자료 학습"

                        LearningStartScreen(
                            navController = navController,
                            goalHours = goalHours,
                            goalMinutes = goalMinutes,
                            selectedLearningType = selectedLearningType
                        )
                    }
                    composable("mypage") {
                        MyPageScreen(navController = navController)
                    }
                    composable("edit_profile") {
                        EditProfileScreen()
                    }
                    composable("datalearningstart") {
                        DataLearningStartScreen(
                            onRegisterNewClick = { navController.navigate("uploadtypeselect") },
                            onUseExistingClick = { navController.navigate("existing") }
                        )
                    }
                    composable("uploadtypeselect") {
                        UploadTypeSelectScreen(
                            onImageClick = {
                                val goalHours = 0
                                val goalMinutes = 30
                                navController.navigate("imageupload/$goalHours/$goalMinutes")
                            },
                            onLinkClick = { navController.navigate("videolinkupload") }
                        )
                    }

                    composable(
                        "imageupload/{goalHours}/{goalMinutes}",
                        arguments = listOf(
                            navArgument("goalHours") { type = NavType.IntType },
                            navArgument("goalMinutes") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0
                        val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 0

                        ImageUploadScreen(
                            onImagesSelected = { uris ->
                                navController.navigate("uploadresult/TEXT/$goalHours/$goalMinutes")
                            },
                            navController = navController,
                            goalHours = goalHours,
                            goalMinutes = goalMinutes
                        )
                    }


                    composable("videolinkupload") { backStackEntry ->
                        // 이전 페이지에서 받은 goalHours와 goalMinutes 추출
                        val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0 // 기본값 0
                        val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 30 // 기본값 30

                        // VideoLinkUploadScreen 호출, goalHours와 goalMinutes를 전달
                        VideoLinkUploadScreen(
                            navController = navController,
                            goalHours = goalHours,
                            goalMinutes = goalMinutes
                        )
                    }



                    composable(
                        route = "uploadresult/{contentId}/{goalHours}/{goalMinutes}",
                        arguments = listOf(
                            navArgument("contentId") { type = NavType.StringType }, // contentId를 Path로 받음
                            navArgument("goalHours") { type = NavType.IntType },
                            navArgument("goalMinutes") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        // 네비게이션에서 받은 파라미터 추출
                        val contentId = backStackEntry.arguments?.getString("contentId") ?: "dummy001"
                        val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0
                        val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 0

                        // API 호출을 위한 상태 변수 설정
                        var learningResponse by remember { mutableStateOf<LearningResponse?>(null) }
                        var isLoading by remember { mutableStateOf(true) }
                        var errorMessage by remember { mutableStateOf<String?>(null) }

                        /*
                        // LaunchedEffect로 비동기 API 호출
                        LaunchedEffect(contentId) {
                            try {
                                // LearningRepository를 사용하여 contentId에 해당하는 콘텐츠 데이터 가져오기
                                val repository = LearningRepository()
                                val response = repository.fetchLearningContent(contentId)

                                when (response) {
                                    is Resource.Success -> {
                                        learningResponse = response.data  // 응답 데이터를 learningResponse에 저장
                                        isLoading = false
                                    }
                                    is Resource.Error -> {
                                        errorMessage = response.message  // 오류 메시지 처리
                                        isLoading = false
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = e.localizedMessage  // 예외 처리
                                isLoading = false
                            }
                        }

                         */

                        // 로딩 중 UI
                        if (isLoading) {
                            CircularProgressIndicator()  // 로딩 화면 표시
                        }
                        // 오류 UI
                        else if (errorMessage != null) {
                            Text(text = "Error: $errorMessage")  // 오류 메시지 표시
                        }
                        // 데이터가 성공적으로 로드된 후 UI
                        else if (learningResponse != null) {
                            val response = learningResponse!!

                            // contentType에 따라 다른 화면을 보여줌
                            when (response.contentType) {
                                "TEXT"-> {
                                    DataLearningMainScreen(
                                        learningResponse = response,
                                        navController = navController,
                                        goalHours = goalHours,
                                        goalMinutes = goalMinutes
                                    )
                                }
                                "VIDEO" -> {
                                    VideoLearningMainScreen(
                                        learningResponse = response,
                                        navController = navController,
                                        goalHours = goalHours,
                                        goalMinutes = goalMinutes
                                    )
                                }
                            }
                        }
                    }

                    composable("library") {
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }

                        //val repository = LearningRepository()
                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context, userPrefs)
                            )
                        )

                        LibraryScreen(navController = navController, viewModel = viewModel)
                    }


                    composable("grade") {
                        GradeTestHomeScreen()
                    }

                    composable("dictation") {
                        DictationScreen()
                    }

                    composable("pronunciation") {
                        PronunciationTestScreen()
                    }

                    composable("summary") {
                        RecommendationScreen()
                    }

                    composable("recommendation") {
                        SummaryTestScreen()
                    }

                    composable("uservocab/{uid}") { backStackEntry ->
                        val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                        val viewModel: LearningViewModel = viewModel(factory = LearningViewModelFactory(repository, repositoryW))

                        VocabScreen(viewModel = viewModel, uid = uid)
                    }

                    composable("main") {
                        BottomBarScreen(navController)
                    }
                    composable(
                        route = "ocr_result/{title}/{text}",
                        arguments = listOf(
                            navArgument("title") { type = NavType.StringType },
                            navArgument("text") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val extractedTitle = backStackEntry.arguments?.getString("title") ?: ""
                        val extractedText = backStackEntry.arguments?.getString("text") ?: ""
                        OcrResultScreen(navController, extractedTitle, extractedText)
                    }
                    composable("videodetail/{id}") { backStackEntry ->
                        val contentId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        // ViewModel 생성
                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context,userPrefs)
                            )
                        )

                        // 전달
                        VideoDetailScreen(
                            viewModel = viewModel,
                            navController = navController,
                            contentId = contentId
                        )
                    }
                    composable("allvideos") {
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }

                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context,userPrefs)
                            )
                        )
                        AllVideoLibraryScreen(navController = navController, viewModel = viewModel)
                    }



                    /*navController.navigate("videodetail/${content.contentId}")*/



                }
            }

        }

    }
    // 구글 로그인 결과 처리
    private fun handleGoogleSignInResult(data: Intent?) {
        val signInAccount = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = signInAccount.result
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(this, "Google 로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

