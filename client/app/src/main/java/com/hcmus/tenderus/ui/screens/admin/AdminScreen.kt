package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.Role
import com.hcmus.tenderus.model.Status
import java.time.LocalDateTime

val accounts = listOf(
    Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
    Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
    Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
    Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
    Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
    Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
    Account("orange", Role.USER, "orange@gmew.com", "0123456789"),
    Account("orange", Role.ADMIN, "orange@gmew.com", "0123456789"),
)

val reports = listOf(
    Report("RP0000", "Hehe", LocalDateTime.now(), Status.REVIEWED),
    Report("RP0001", "Hehe", LocalDateTime.now(), Status.PENDING),
    Report("RP0002", "Hehe", LocalDateTime.now(), Status.REVIEWED),
    Report("RP0003", "Hehe", LocalDateTime.now(), Status.PENDING),
    Report("RP0004", "Hehe", LocalDateTime.now(), Status.REVIEWED),
    Report("RP0005", "Hehe", LocalDateTime.now(), Status.PENDING),
    Report("RP0006", "Hehe", LocalDateTime.now(), Status.REVIEWED),
    Report("RP0007", "Hehe", LocalDateTime.now(), Status.PENDING),
    Report("RP0008", "Hehe", LocalDateTime.now(), Status.REVIEWED),
    Report("RP0009", "Hehe", LocalDateTime.now(), Status.PENDING),
)

@Composable
fun AdminScreen() {
    val navController = rememberNavController()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavHost(navController = navController, startDestination = BottomNavScreen.Dashboard.route) {
            composable(BottomNavScreen.Dashboard.route) { DashboardScreen() }
            composable(BottomNavScreen.Analytics.route) { ReportListScreen(reports) }
            composable(BottomNavScreen.Users.route) { AccountListScreen(accounts ) }
            composable(BottomNavScreen.Logout.route) { LogoutScreen() }
        }
        BottomNavigationBar(navController = navController)
    }
}

// temp
@Composable
fun DashboardScreen() {
    // Your content for the Dashboard screen
}


@Composable
fun LogoutScreen() {
    // Handle logout logic
}
