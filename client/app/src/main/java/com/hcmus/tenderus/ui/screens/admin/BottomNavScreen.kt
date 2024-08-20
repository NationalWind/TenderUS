package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val icon: ImageVector, val contentDescription: String) {
    object Dashboard : BottomNavScreen("dashboard", Icons.Filled.PieChart, "Dashboard")
    object Analytics : BottomNavScreen("analytics", Icons.Filled.BarChart, "Analytics")
    object Users : BottomNavScreen("users", Icons.Filled.People, "Users")
    object Logout : BottomNavScreen("logout", Icons.AutoMirrored.Filled.Logout, "Logout")
}
