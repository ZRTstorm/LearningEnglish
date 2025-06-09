package com.example.learningenglish.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.learningenglish.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import com.example.learningenglish.ui.auth.UserPreferencesDataStore
import kotlinx.coroutines.flow.firstOrNull


@Composable
fun CustomBottomBar(navController: NavController, currentRoute: String) {
    val context = LocalContext.current
    val userPrefs = remember { UserPreferencesDataStore(context) }
    var userId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        userId = userPrefs.getUserId().firstOrNull()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .shadow(8.dp, shape = RoundedCornerShape(32.dp))
            .background(Color.White, shape = RoundedCornerShape(32.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomBarItem("home", Icons.Filled.Home, "홈", currentRoute == "home") {
                navController.navigate("home")
            }
            BottomBarItem("library", Icons.Filled.MenuBook, "라이브러리", currentRoute == "library") {
                navController.navigate("library")
            }

            // 중앙 + 버튼
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(6.dp, shape = CircleShape)
                    .background(color = Color(0xFF673AB7), shape = CircleShape)
                    .clickable { navController.navigate("datalearningstart") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "추가",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            BottomBarItem("search", Icons.Filled.Search, "검색", currentRoute == "search") {
                navController.navigate("search")
            }
            if (userId != null) {
                BottomBarItem(
                    route = "uservocab/${userId}",
                    icon = Icons.Filled.Search,
                    label = "단어장",
                    selected = currentRoute == "uservocab/${userId}"
                ) {
                    navController.navigate("uservocab/${userId}")
                }
            }
        }
    }
}

@Composable
fun BottomBarItem(
    route: String,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color.Black else Color.Gray
        )
        Text(
            text = label,
            color = if (selected) Color.Black else Color.Gray,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

data class BottomNavItem(val label: String, val route: String, val iconRes: Int)


@Composable
fun BottomBarIcon(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) Color(0xFF6A5ACD) else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) Color(0xFF6A5ACD) else Color.Gray
        )
    }
}
