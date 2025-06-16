package com.example.learningenglish.ui.ScreenNavigation

import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(navController: NavController, items: List<BottomNavItem>) {
    NavigationBar(
        modifier = Modifier.height(0.dp),
        containerColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) }
            )
        }
    }
}
