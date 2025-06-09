package com.example.learningenglish.ui.learning.DataLearning.upload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.learningenglish.R

@Composable
fun UploadTypeSelectScreen(
    onImageClick: () -> Unit,
    onLinkClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically)
    ) {
        Text(
            "등록 방식을 선택하세요",
            style = MaterialTheme.typography.headlineSmall
        )

        UploadOptionCard(
            title = "이미지로 등록",
            iconRes = R.drawable.ic_uploadimage,
            onClick = onImageClick,
            backgroundColor = Color(0xFFD1C4E9)
        )

        UploadOptionCard(
            title = "영상/음성 링크로 등록",
            iconRes = R.drawable.ic_uploadlink,
            onClick = onLinkClick,
            backgroundColor = Color(0xFFB2EBF2)
        )
    }
}

@Composable
fun UploadOptionCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    backgroundColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, style = MaterialTheme.typography.titleMedium)
        }
    }
}

