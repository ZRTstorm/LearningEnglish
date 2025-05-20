package com.example.learningenglish.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GoalSettingDialog(
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "학습 목표를 설정할까요?", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("시간: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    NumberPicker(value = hours, range = 0..10, onValueChange = { hours = it })
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("분: ")
                    Spacer(modifier = Modifier.width(8.dp))
                    NumberPicker(value = minutes, range = 0..59, onValueChange = { minutes = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(hours, minutes) }) {
                Text("확인")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("취소")
            }
        },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.LightGray
    )
}

@Composable
fun NumberPicker(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { if (value > range.first) onValueChange(value - 1) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Down")
        }
        Text(text = value.toString(), style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = { if (value < range.last) onValueChange(value + 1) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Up")
        }
    }
}

