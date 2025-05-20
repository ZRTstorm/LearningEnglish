package com.example.learningenglish.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.learningenglish.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth


@Composable
fun FeatureCard(iconRes: Int, label: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,            // 기본 elevation
            pressedElevation = 8.dp,            // 눌렸을 때 elevation
            focusedElevation = 6.dp,            // 포커스 되었을 때 elevation
            hoveredElevation = 6.dp,            // 마우스를 올렸을 때 elevation
            draggedElevation = 10.dp,           // 드래그 중일 때 elevation
            disabledElevation = 2.dp            // 비활성화 되었을 때 elevation
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label)
        }
    }
}

@Composable
fun FeatureSelectionScreen(
    navController: NavController
) {
    val userUid = FirebaseAuth.getInstance().currentUser?.uid

    val features = listOf(
        "자료 학습" to R.drawable.ic_learn,
        "받아 쓰기" to R.drawable.ic_dictation,
        "발음 평가" to R.drawable.ic_pronunciation,
        "라이브러리" to R.drawable.ic_summary,
        "단어장" to R.drawable.ic_vocab,
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(features) { feature ->
            FeatureCard(
                label = feature.first,  // feature.first로 label 접근
                iconRes = feature.second,  // feature.second로 아이콘 리소스 접근
                onClick = {
                    when (feature.first) {
                        "자료 학습" -> navController.navigate("datalearningstart")
                        "받아 쓰기" -> navController.navigate("dictation")
                        "발음 평가" -> navController.navigate("pronunciation")
                        "라이브러리" -> navController.navigate("library")
                        "단어장" -> if (userUid != null) {
                            navController.navigate("uservocab/$userUid")
                        }
                    }
                }
            )
        }
    }
}







