package com.example.learningenglish

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
  import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.learningenglish.ui.recommendation.RecommendationScreen
import com.example.learningenglish.ui.learning.summary.SummaryTestScreen
import com.example.learningenglish.ui.theme.LearningEnglishTheme
import com.example.learningenglish.ui.learning.vocabulary.VocabScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.learningenglish.data.repository.WordRepository
import com.example.learningenglish.ui.auth.AttendancePreferencesDataStore
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import com.example.learningenglish.ui.learning.DataLearning.main.TextDetailScreen
import com.example.learningenglish.ui.learning.DataLearning.main.VideoDetailScreen
import com.example.learningenglish.ui.learning.DataLearning.upload.OcrResultScreen
import com.example.learningenglish.ui.learning.dictation.DictationResultScreen
import com.example.learningenglish.ui.learning.dictation.DictationSentenceTypeSelectScreen
import com.example.learningenglish.ui.learning.pronunciation.PronunciationRecordScreen
import com.example.learningenglish.ui.learning.pronunciation.PronunciationResultScreen
import com.example.learningenglish.ui.learning.pronunciation.PronunciationSentenceSelectScreen
import com.example.learningenglish.ui.quiz.InsertionQuizScreen
import com.example.learningenglish.ui.quiz.InsertionResultScreen
import com.example.learningenglish.ui.quiz.OrderQuizResultScreen
import com.example.learningenglish.ui.quiz.OrderQuizScreen
import com.example.learningenglish.ui.quiz.QuizTypeSelectScreen
//import com.example.learningenglish.ui.learning.pronunciation.PronunciationStartAndEvalScreen
//import com.example.learningenglish.ui.quiz.MixedQuizScreen
import com.example.learningenglish.ui.recommendation.AllTextLibraryScreen
import com.example.learningenglish.ui.recommendation.AllVideoLibraryScreen
import com.example.learningenglish.ui.recommendation.LibraryScreen
import com.example.learningenglish.ui.recommendation.MyTextLibraryScreen
import com.example.learningenglish.ui.recommendation.MyVideoLibraryScreen
import com.example.learningenglish.ui.recommendation.SearchScreen
import com.example.learningenglish.ui.recommendation.SelectLearningModeScreen
import com.example.learningenglish.ui.recommendation.SelectLearningModeVideoScreen
import com.example.learningenglish.ui.recommendation.SimilarContentScreen
import com.example.learningenglish.ui.record.DictationHistoryScreen
import com.example.learningenglish.ui.record.PronunciationHistoryScreen
import com.example.learningenglish.ui.record.QuizHistoryScreen
import com.example.learningenglish.ui.record.QuizRecordDetailScreen
import com.example.learningenglish.ui.record.QuizRecordScreen
import com.example.learningenglish.ui.record.RecordScreen
import com.example.learningenglish.viewmodel.AuthViewModel
import com.example.learningenglish.viewmodel.AuthViewModelFactory
import com.example.learningenglish.viewmodel.LearningViewModel
import com.example.learningenglish.viewmodel.LearningViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.firstOrNull
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

        // 녹음 권한 요청
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1001
        )



        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            LearningEnglishTheme {

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val coroutineScope = rememberCoroutineScope()
                val authManager = AuthManager(this)

                val navHostController = navController as NavHostController
                navHostController.setViewModelStore(this.viewModelStore) // ViewModelStore 설정

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

                        val AuthviewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authManager, userPrefs))

                        HomeScreen(
                            navController = navController,
                            viewModel = AuthviewModel,
                            learningViewModel = viewModel
                        )
                    }

                    composable("featureselection") {
                        FeatureSelectionScreen(navController = navController)
                    }
                    composable(
                        route = "learningstart/{selectedLearningType}",
                        arguments = listOf(
                            //navArgument("goalHours") { type = NavType.IntType },
                            //navArgument("goalMinutes") { type = NavType.IntType },
                            navArgument("selectedLearningType") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        //val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0
                        //val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 0
                        val selectedLearningType = backStackEntry.arguments?.getString("selectedLearningType") ?: "자료 학습"

                        LearningStartScreen(
                            navController = navController,
                            //goalHours = goalHours,
                            //goalMinutes = goalMinutes,
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
                            onUseExistingClick = { navController.navigate("existing") },
                            navController = navController
                        )
                    }
                    composable("uploadtypeselect") {
                        UploadTypeSelectScreen(
                            onImageClick = {
                                val goalHours = 0
                                val goalMinutes = 30
                                navController.navigate("imageupload")
                            },
                            onLinkClick = { navController.navigate("videolinkupload") }
                        )
                    }

                    composable(
                        "imageupload",
                        arguments = listOf(
                            //navArgument("goalHours") { type = NavType.IntType },
                            //navArgument("goalMinutes") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        //val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0
                        //val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 0

                        ImageUploadScreen(
                            onImagesSelected = { uris ->
                                navController.navigate("uploadresult/TEXT")
                            },
                            navController = navController,
                            //goalHours = goalHours,
                            //goalMinutes = goalMinutes
                        )
                    }


                    composable("videolinkupload") { backStackEntry ->
                        // 이전 페이지에서 받은 goalHours와 goalMinutes 추출
                        //val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0 // 기본값 0
                        //val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 30 // 기본값 30

                        // VideoLinkUploadScreen 호출, goalHours와 goalMinutes를 전달
                        VideoLinkUploadScreen(
                            navController = navController,
                            //goalHours = goalHours,
                            //goalMinutes = goalMinutes
                        )
                    }



                    composable(
                        route = "uploadresult/{contentId}",
                        arguments = listOf(
                            navArgument("contentId") { type = NavType.StringType }, // contentId를 Path로 받음
                            //navArgument("goalHours") { type = NavType.IntType },
                            //navArgument("goalMinutes") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        // 네비게이션에서 받은 파라미터 추출
                        val contentId = backStackEntry.arguments?.getString("contentId") ?: "dummy001"
                        //val goalHours = backStackEntry.arguments?.getInt("goalHours") ?: 0
                        //val goalMinutes = backStackEntry.arguments?.getInt("goalMinutes") ?: 0

                        // API 호출을 위한 상태 변수 설정
                        var learningResponse by remember { mutableStateOf<LearningResponse?>(null) }
                        var isLoading by remember { mutableStateOf(true) }
                        var errorMessage by remember { mutableStateOf<String?>(null) }

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
                                        //goalHours = goalHours,
                                        //goalMinutes = goalMinutes
                                    )
                                }
                                "VIDEO" -> {
                                    VideoLearningMainScreen(
                                        learningResponse = response,
                                        navController = navController,
                                        //goalHours = goalHours,
                                        //goalMinutes = goalMinutes
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
                                WordRepository(context, userPrefs),
                                attendancePrefs = attendancePrefs
                            )
                        )

                        LibraryScreen(navController = navController, viewModel = viewModel)
                    }


                    composable("grade") {
                        GradeTestHomeScreen()
                    }

                    composable(
                        route = "dictation_sentence_type/{contentsType}/{contentId}",
                        arguments = listOf(
                            navArgument("contentsType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "text"
                        val contentId = backStackEntry.arguments?.getInt("contentId") ?: 0
                        DictationSentenceTypeSelectScreen(navController, contentId, contentsType, viewModel)
                    }

                    composable(
                        "dictation/{contentId}/{contentsType}/{sentenceLevel}",
                        arguments = listOf(
                            navArgument("contentId") { type = NavType.IntType },
                            navArgument("contentsType") { type = NavType.StringType },
                            navArgument("sentenceLevel") { type = NavType.IntType },
                            //navArgument("sentenceId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentId = backStackEntry.arguments?.getInt("contentId") ?: 1
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "text"
                        val sentenceLevel = backStackEntry.arguments?.getInt("sentenceLevel") ?: 1
                        val sentenceId = backStackEntry.arguments?.getInt("sentenceId") ?: "1"

                        DictationScreen(
                            viewModel = viewModel,
                            navController = navController,
                            contentId = contentId,
                            contentsType = contentsType,
                            sentenceLevel = sentenceLevel,
                        )
                    }
                    composable("dictation_result") {
                        val result = viewModel.lastDictationResult

                        if (result != null) {
                            DictationResultScreen(
                                navController = navController,
                                reference = result.reference,
                                userInput = result.userInput,
                                score = result.accuracyScore,
                                grammarScore = result.grammarScore,
                                incorrectWords = result.incorrectWords,
                                feedbackMessages = result.feedbackMessages,
                                contentId = result.contentId,
                                contentsType = result.contentsType,
                                filePaths = result.filePaths,
                                viewModel = viewModel
                            )
                        } else {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("결과 데이터가 없습니다.")
                            }
                        }
                    }
                    // 1. 문장 선택 화면
                    composable("pronunciation_sentence_type/{contentType}/{contentId}",
                        arguments = listOf(
                            navArgument("contentType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: ""
                        val contentId = backStackEntry.arguments?.getInt("contentId") ?: 0

                        PronunciationSentenceSelectScreen(
                            navController = navController,
                            contentId = contentId,
                            contentsType = contentType,
                            viewModel = viewModel
                        )
                    }

// 2. 녹음 화면
                    composable("pronunciation/record/{contentType}/{contentId}/{sentenceLevel}",
                        arguments = listOf(
                            navArgument("contentType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: ""
                        val contentId = backStackEntry.arguments?.getInt("contentId") ?: 0
                        val sentenceLevel = backStackEntry.arguments?.getInt("sentenceLevel") ?: 1

                        PronunciationRecordScreen(
                            viewModel = viewModel,
                            navController = navController,
                            contentId = contentId,
                            contentType = contentType,
                            sentenceLevel = sentenceLevel
                        )
                    }

// 3. 결과 화면
                    composable("pronunciation/result/{contentType}/{contentId}",
                        arguments = listOf(
                            navArgument("contentType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: ""
                        val contentId = backStackEntry.arguments?.getInt("contentId") ?: 0

                        PronunciationResultScreen(
                            viewModel = viewModel,
                            navController = navController,
                            contentId = contentId,
                            contentsType = contentType
                        )
                    }
                    /*
                    composable(
                        "dictation_result/{reference}?score={score}&user={userInput}",
                        arguments = listOf(
                            navArgument("reference") { type = NavType.StringType },
                            navArgument("score") { type = NavType.StringType },
                            navArgument("userInput") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val reference = backStackEntry.arguments?.getString("reference") ?: ""
                        val score = backStackEntry.arguments?.getString("score")?.toDoubleOrNull() ?: 0.0
                        val userInput = backStackEntry.arguments?.getString("userInput") ?: ""

                        DictationResultScreen(
                            navController = navController,
                            reference = reference,
                            userInput = userInput,
                            score = score
                        )
                    }*/



                    /*
                    composable("pronunciation_eval/{userId}/{contentType}/{contentId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: "text"
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: return@composable

                        PronunciationStartAndEvalScreen(
                            viewModel = viewModel,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId,
                            navController = navController
                        )
                    }

                     */

                    composable("pronunciation_history/{userId}/{contentType}/{contentId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: "text"
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: return@composable

                        PronunciationHistoryScreen(
                            viewModel = viewModel,
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId
                        )
                    }

                    composable("dictation_history/{userId}/{contentType}/{contentId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: "text"
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: return@composable

                        DictationHistoryScreen(
                            viewModel = viewModel,
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId
                        )
                    }


                    composable("similar_content/{contentsType}/{contentId}") { backStackEntry ->
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "text"
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: 0
                        SimilarContentScreen(contentsType, contentId, navController, viewModel)
                    }

                    composable("search") {
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        val coroutineScope = rememberCoroutineScope()
                        var userId by remember { mutableStateOf<Int?>(null) }

                        LaunchedEffect(Unit) {
                            userId = userPrefs.getUserId().firstOrNull()
                        }

                        userId?.let {
                            SearchScreen(navController = navController, userId = it, viewModel = viewModel)
                        }
                    }


                    /*
                    composable("quiz/{userId}/{contentsType}/{contentId}") {backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "text"
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: 0
                        //val type = it.arguments?.getString("type")!!
                        //val id = it.arguments?.getString("id")!!.toInt()
                        MixedQuizScreen(navController, userId, contentsType, contentId, viewModel)
                    }
                     */

                    composable("quiz_select/{userId}/{contentType}/{contentId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: return@composable
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: return@composable

                        QuizTypeSelectScreen(
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId
                        )
                    }
                    // 삽입 퀴즈 화면
                    composable("insertion_quiz/{userId}/{contentType}/{contentId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: return@composable
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: return@composable

                        InsertionQuizScreen(
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId,
                            viewModel = viewModel
                        )
                    }
                    composable(
                        "insertion_result/{userId}/{contentType}/{contentId}/{quizId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.IntType },
                            navArgument("contentType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType },
                            navArgument("quizId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments!!.getInt("userId")
                        val contentType = backStackEntry.arguments!!.getString("contentType")!!
                        val contentId = backStackEntry.arguments!!.getInt("contentId")
                        val quizId = backStackEntry.arguments!!.getInt("quizId")

                        InsertionResultScreen(
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId,
                            quizId = quizId,
                            viewModel = viewModel // 동일한 ViewModel 전달
                        )
                    }

                    // 배열 퀴즈 화면
                    composable("summaOrders_quiz/{userId}/{contentType}/{contentId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: return@composable
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: return@composable

                        OrderQuizScreen(
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId,
                            viewModel = viewModel
                        )
                    }
                    composable(
                        "summaOrders_result/{userId}/{contentType}/{contentId}/{quizId}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.IntType },
                            navArgument("contentType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType },
                            navArgument("quizId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments!!.getInt("userId")
                        val contentType = backStackEntry.arguments!!.getString("contentType")!!
                        val contentId = backStackEntry.arguments!!.getInt("contentId")
                        val quizId = backStackEntry.arguments!!.getInt("quizId")

                        OrderQuizResultScreen(
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId,
                            quizId = quizId,
                            viewModel = viewModel // 동일한 ViewModel 전달
                        )
                    }


                    // NavGraph route 선언 예시
                    composable(
                        route = "quiz_history/{userId}/{contentType}/{contentId}?latestQuizId={latestQuizId}",
                        arguments = listOf(
                            navArgument("latestQuizId") {
                                type = NavType.IntType
                                defaultValue = -1 // 안 보낸 경우 처리
                            }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")!!.toInt()
                        val contentType = backStackEntry.arguments?.getString("contentType")!!
                        val contentId = backStackEntry.arguments?.getString("contentId")!!.toInt()
                        val latestQuizId = backStackEntry.arguments?.getInt("latestQuizId")?.takeIf { it != -1 }

                        QuizHistoryScreen(
                            viewModel,
                            navController,
                            userId,
                            contentType,
                            contentId,
                            latestQuizId
                        )
                    }


                    /*
                    composable("quiz_history/{userId}/{contentType}/{contentId}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull() ?: return@composable
                        val contentType = backStackEntry.arguments?.getString("contentType") ?: "text"
                        val contentId = backStackEntry.arguments?.getString("contentId")?.toIntOrNull() ?: return@composable

                        QuizHistoryScreen(
                            viewModel = viewModel,
                            navController = navController,
                            userId = userId,
                            contentType = contentType,
                            contentId = contentId
                        )
                    }

                     */


                    /*
                    composable("quiz_history/{contentsType}/{contentId}") {backStackEntry ->
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        var userId by remember { mutableStateOf(0) }

                        LaunchedEffect(Unit) {
                            userId = userPrefs.getUserId().firstOrNull() ?: 0
                        }
                        QuizRecordScreen(navController = navController, viewModel = viewModel, userId = userId)
                    }

                    composable("quizDetail/{libraryId}") { backStackEntry ->
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        val libraryId = backStackEntry.arguments?.getString("libraryId")?.toIntOrNull() ?: return@composable
                        var userId by remember { mutableStateOf(0) }

                        LaunchedEffect(Unit) {
                            userId = userPrefs.getUserId().firstOrNull() ?: 0
                        }
                        QuizRecordDetailScreen(libraryId = libraryId, viewModel = viewModel, userId = userId)
                    }

                     */


                    composable("summary") {
                        RecommendationScreen()
                    }

                    composable("recommendation") {
                        SummaryTestScreen()
                    }

                    composable("record") {
                        RecordScreen(navController = navController)
                    }

                    composable("uservocab/{userId}") { backStackEntry ->
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        var userId by remember { mutableStateOf(0) }

                        LaunchedEffect(Unit) {
                            userId = userPrefs.getUserId().firstOrNull() ?: 0
                        }
                        //val uid = backStackEntry.arguments?.getString("uid") ?: return@composable
                        val viewModel: LearningViewModel = viewModel(factory = LearningViewModelFactory(repository, repositoryW, attendancePrefs ))

                        VocabScreen(viewModel = viewModel, navController = navController, userId = userId)
                    }

                    composable("main") {
                        BottomBarScreen(navController)
                    }
                    composable(
                        route = "ocr_result/{userId}/{title}/{text}",
                        arguments = listOf(
                            navArgument("userId") { type = NavType.IntType },
                            navArgument("title") { type = NavType.StringType },
                            navArgument("text") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId")
                        val extractedTitle = backStackEntry.arguments?.getString("title") ?: ""
                        val extractedText = backStackEntry.arguments?.getString("text") ?: ""
                        OcrResultScreen(navController, userId, extractedTitle, extractedText)
                    }

                    /*
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
                            contentId = contentId,
                            contentsType = contentsType
                        )
                    }
                     */

                    composable("allvideos") {
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }

                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context,userPrefs),
                                attendancePrefs = attendancePrefs
                            )
                        )
                        AllVideoLibraryScreen(navController = navController, viewModel = viewModel)
                    }

                    composable("alltexts") {
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }

                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context,userPrefs),
                                attendancePrefs = attendancePrefs
                            )
                        )
                        AllTextLibraryScreen(navController = navController, viewModel = viewModel)
                    }

                    composable("textdetail/{contentsType}/{id}",
                        arguments = listOf(
                            navArgument("contentsType") { type = NavType.StringType },
                            navArgument("id") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "text"
                        val contentId = backStackEntry.arguments?.getInt("id") ?: return@composable

                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        // ViewModel 생성
                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context,userPrefs),
                                attendancePrefs = attendancePrefs
                            )
                        )

                        // 전달
                        TextDetailScreen(
                            viewModel = viewModel,
                            navController = navController,
                            contentId = contentId,
                            contentsType = contentsType
                        )
                    }
                    composable("videodetail/{contentsType}/{id}",
                        arguments = listOf(
                            navArgument("contentsType") { type = NavType.StringType },
                            navArgument("id") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "video"
                        val contentId = backStackEntry.arguments?.getInt("id") ?: return@composable

                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }
                        // ViewModel 생성
                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context,userPrefs),
                                attendancePrefs = attendancePrefs
                            )
                        )

                        // 전달
                        VideoDetailScreen(
                            viewModel = viewModel,
                            navController = navController,
                            contentId = contentId,
                            contentsType = contentsType
                        )
                    }

                    composable("alltexts") {
                        val context = LocalContext.current
                        val userPrefs = remember { UserPreferencesDataStore(context) }

                        val viewModel: LearningViewModel = viewModel(
                            factory = LearningViewModelFactory(
                                LearningRepository(),
                                WordRepository(context,userPrefs),
                                attendancePrefs = attendancePrefs
                            )
                        )
                        AllTextLibraryScreen(navController = navController, viewModel = viewModel)
                    }
                    composable("mytexts") {
                        MyTextLibraryScreen(navController, viewModel)
                    }

                    composable("myvideos") {
                        MyVideoLibraryScreen(navController, viewModel)
                    }
                    composable(
                        route = "select_mode/{contentsType}/{contentId}",
                        arguments = listOf(
                            navArgument("contentsType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "text"
                        val contentId = backStackEntry.arguments?.getInt("contentId") ?: 0
                        SelectLearningModeScreen(navController, contentId, contentsType)
                    }
                    composable(
                        route = "select_mode2/{contentsType}/{contentId}",
                        arguments = listOf(
                            navArgument("contentsType") { type = NavType.StringType },
                            navArgument("contentId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val contentsType = backStackEntry.arguments?.getString("contentsType") ?: "video"
                        val contentId = backStackEntry.arguments?.getInt("contentId") ?: 0
                        SelectLearningModeVideoScreen(navController, contentId, contentsType)
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

