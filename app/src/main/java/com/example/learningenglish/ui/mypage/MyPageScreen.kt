package com.example.learningenglish.ui.mypage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MyPageScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("홍길동님 안녕하세요!", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { navController.navigate("edit_profile") }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Your Favorite Words", style = MaterialTheme.typography.titleMedium)

        LazyColumn(
            modifier = Modifier.fillMaxHeight(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(5) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Word $index", style = MaterialTheme.typography.titleSmall)
                        Text("Found in: Video ABC", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}