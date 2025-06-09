package com.example.learningenglish.ui.learning.DataLearning.upload

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.learningenglish.data.model.AudioData
import com.example.learningenglish.data.remote.RetrofitInstance
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLinkUploadScreen(
    //handleSubmit: (String, String) -> Unit,  // handleSubmit 매개변수 추가
    navController: NavController,
    //goalHours: Int,
    //goalMinutes: Int
) {
    var url by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("링크 등록") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("학습할 영상/음성 링크를 입력하세요", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("링크 주소") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (url.isNotBlank() && title.isNotBlank()) {
                        Toast.makeText(context, "등록 중입니다..", Toast.LENGTH_SHORT).show()
                        navController.navigate("home")

                        coroutineScope.launch {
                            Log.d("DEBUG", "getUserId 호출됨")
                            val userId = userPrefs.getUserId().firstOrNull()
                            Log.d("DEBUG", "userId 수신값: $userId")
                            if (userId != null) {
                                val audioData = AudioData(
                                    url = url,
                                    title = title,
                                    userId = userId
                                )

                                Log.d("API_CALL", "Sending POST request to /api/audio/process with data: $audioData")

                                try {
                                    val response = RetrofitInstance.api.uploadAudio(audioData)
                                    if (response.isSuccessful) {
                                        //val learningResponse = response.body()
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "등록이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "등록 완료: ${response.message()}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(context, "등록 중 오류 발생", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(context, "등록 중 오류 발생", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                enabled = url.isNotBlank() && title.isNotBlank()
            ) {
                Text("등록")
            }
        }
    }
}






