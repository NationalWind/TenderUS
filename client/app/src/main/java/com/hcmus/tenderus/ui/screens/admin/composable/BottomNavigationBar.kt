package com.hcmus.tenderus.ui.screens.admin.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.ui.theme.TenderUSTheme

sealed class BottomNav(
    val route: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    data object Analytics : BottomNav(
        route = "analytics",
        icon = Icons.Filled.BarChart,
        contentDescription = "Analytics"
    )

    data object Reports : BottomNav(
        route = "reports",
        icon = Icons.Filled.Report,
        contentDescription = "Reports"
    )

    data object Users : BottomNav(
        route = "users",
        icon = Icons.Filled.People,
        contentDescription = "Users"
    )

    data object Settings : BottomNav(
        route = "settings",
        icon = Icons.Filled.Settings,
        contentDescription = "Settings"
    )
}

@Composable
fun BottomNavigationBar(navController: NavController, modifier: Modifier = Modifier) {
    val screens = listOf(
        BottomNav.Analytics,
        BottomNav.Reports,
        BottomNav.Users,
        BottomNav.Settings
    )

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .shadow(0.5.dp)
        )
        NavigationBar(containerColor = Color.Transparent, modifier = modifier) {
            val navBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry.value?.destination?.route

            screens.forEach { screen ->
                NavigationBarItem(
                    icon = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(screen.icon, contentDescription = screen.contentDescription)
                            Text(text = screen.contentDescription, fontSize = 12.sp)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    ),
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    TenderUSTheme {
        BottomNavigationBar(navController = rememberNavController())
    }
}
