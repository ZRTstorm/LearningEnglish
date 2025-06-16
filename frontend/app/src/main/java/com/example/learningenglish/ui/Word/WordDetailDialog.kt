package com.example.learningenglish.ui.Word

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.learningenglish.data.model.WordDetailResponse

@Composable
fun WordDetailDialog(
    word: String,
    onClose: () -> Unit,
    onFavorite: () -> Unit,
    wordInfo: WordDetailResponse?
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = { Text(word) },
        text = {
            if (wordInfo != null) {
                Column {
                    Text("${wordInfo.phonetic}", color = Color(0xFF757575))
                    wordInfo.definitions.forEach {
                        Text("${wordInfo.definitions.indexOf(it) + 1}.  ${it.definitionKo}", fontSize = 16.sp)
                        it.exampleEn?.let { en ->
                            Text(" $en", style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
                        }
                        it.exampleKo?.let { ko ->
                            Text(" $ko", style = MaterialTheme.typography.bodySmall, color = Color(0xFF757575))
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    /*
                    Text("발음: ${wordInfo.phonetic}")
                    wordInfo.definitions.forEach {
                        Text("뜻: ${it.definitionKo}")
                        Text("예시(영어): ${it.exampleEn}", style = MaterialTheme.typography.bodySmall)
                        Text("예시(한글): ${it.exampleKo}", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                     */
                }
            } else {
                CircularProgressIndicator()
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onFavorite()
                onClose()
            }) {
                Text("단어장에 추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) {
                Text("닫기")
            }
        }
    )
}
