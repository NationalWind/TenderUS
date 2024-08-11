package com.hcmus.tenderus.ui.screens.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.R
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
    var showBar by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            if (showBar) BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Add an optional logo at the top of the screen, similar to MainScreen
            if (showBar) {
                Image(
                    painter = painterResource(id = R.drawable.logo_mainscreen), // Replace with your logo resource
                    contentDescription = "Admin Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp) // Add padding as needed
                        .size(30.dp) // Adjust size as needed
                )
            }

            // Main content (NavHost)
            Box {
                NavHost(
                    navController = navController,
                    startDestination = BottomNavScreen.Dashboard.route
                ) {
                    composable(BottomNavScreen.Dashboard.route) {
                        LaunchedEffect(Unit) {
                            showBar = true
                        }
                        DashboardScreen()
                    }
                    composable(BottomNavScreen.Analytics.route) {
                        LaunchedEffect(Unit) {
                            showBar = true
                        }
                        ReportListScreen(reports)
                    }
                    composable(BottomNavScreen.Users.route) {
                        LaunchedEffect(Unit) {
                            showBar = true
                        }
                        AccountListScreen(accounts)
                    }
                    composable(BottomNavScreen.Logout.route) {
                        LaunchedEffect(Unit) {
                            showBar = false
                        }
                        LogoutScreen()
                    }
                }
            }
        }
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
