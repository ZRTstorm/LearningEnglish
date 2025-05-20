package com.example.learningenglish.ui.auth

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.learningenglish.R
import com.example.learningenglish.viewmodel.AuthViewModel
import com.example.learningenglish.viewmodel.AuthViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavHostController,
    onLoginClick: (String, String) -> Unit,
    onGoogleLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    val activity = context as Activity
    val coroutineScope = rememberCoroutineScope()
    val authManager = remember { AuthManager(context) }
    val userPrefs = remember { UserPreferencesDataStore(context) }


    val viewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(authManager, userPrefs))

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        if (task.isSuccessful) {
            coroutineScope.launch {
                val user = authManager.firebaseAuthWithGoogle(task.result)
                message = "Google 로그인 성공: ${user?.displayName}"
                viewModel.authenticateWithServer() // ✅ 서버 인증 요청 추가
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        } else {
            message = "Google 로그인 실패"
        }
    }

    fun tryLogin() {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(context, "이메일과 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            return
        }

        isLoading = true

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    viewModel.authenticateWithServer() // ✅ 서버 인증 요청 추가
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            } else {
                Toast.makeText(context, "로그인 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFEAF0FF), Color(0xFFD7DBFF))))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_mainlogo),
            contentDescription = "앱 로고",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "LearnEnglish",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF3F51B5)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "AI 기반의 언어 능력 향상 플랫폼",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("이메일") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("비밀번호") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { tryLogin() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("로그인")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = {  authManager.signInWithGoogle(activity, googleLauncher) },
                //onGoogleLoginClick()
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(painterResource(id = R.drawable.ic_google), contentDescription = "Google", tint = Color.Unspecified)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        TextButton(onClick = { navController.navigate("signUp") }) {
            Text("계정이 없으신가요? 회원가입")
        }
        TextButton(onClick = onForgotPasswordClick) {
            Text("비밀번호를 잊으셨나요?")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        navController = rememberNavController(),
        onLoginClick = { _, _ -> },
        onGoogleLoginClick = {},
        onSignUpClick = {},
        onForgotPasswordClick = {}
    )
}
