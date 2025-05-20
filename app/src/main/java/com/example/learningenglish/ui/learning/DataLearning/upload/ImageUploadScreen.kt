package com.example.learningenglish.ui.learning.DataLearning.upload

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.example.learningenglish.R
import com.example.learningenglish.data.util.recognizeTextFromBitmap
import com.example.learningenglish.data.util.uriToBitmap
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageUploadScreen(
    onImagesSelected: (List<Uri>) -> Unit,
    navController: NavController,
    goalHours: Int,      // <-- 추가
    goalMinutes: Int
) {
    var title by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            coroutineScope.launch {
                val texts = mutableListOf<String>()
                for (uri in uris) {
                    val bitmap = uriToBitmap(context, uri)
                    if (bitmap != null) {
                        try {
                            val text = recognizeTextFromBitmap(bitmap)
                            texts.add(text)
                        } catch (e: Exception) {
                            Log.e("OCR", "Text recognition failed", e)
                        }
                    }
                }
                Log.d("OCR Result", texts.joinToString("\n"))
                val joinedText = texts.joinToString("\n")
                val encodedText = Uri.encode(joinedText)
                val encodedTitle = Uri.encode(title)
                // 필요 시 texts를 서버로 전송하거나 화면에 표시
                navController.navigate("ocr_result/$encodedTitle/$encodedText")

            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("이미지 등록") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
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
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목을 입력하세요") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("학습에 사용할 이미지를 선택하세요", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    launcher.launch("image/*") // 여러 장 선택 가능
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_uploadimage),
                    contentDescription = "Upload Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("이미지 선택하기")
            }
        }
    }
}
