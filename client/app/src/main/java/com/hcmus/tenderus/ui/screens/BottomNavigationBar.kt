package com.hcmus.tenderus.ui.screens

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Discover,
        BottomNavItem.Matches,
        BottomNavItem.Explore,
        BottomNavItem.Chat,
        BottomNavItem.Profile
    )

    NavigationBar(
        containerColor = Color.Transparent,
        contentColor = Color.Gray
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(24.dp)
                    )
                },
                label = { Text(text = item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFB71C1C),
                    selectedTextColor = Color(0xFFB71C1C),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor =  Color(0xFFFFE4E1)
                ),
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }
    }
}
