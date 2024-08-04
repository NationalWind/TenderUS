package com.hcmus.tenderus.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.R
import com.hcmus.tenderus.ui.screens.discover.DiscoverScreen
import com.hcmus.tenderus.ui.screens.discover.MatchesScreen
import com.hcmus.tenderus.ui.screens.discover.MessageScreen
import com.hcmus.tenderus.ui.screens.explore.ExploreScreen
import com.hcmus.tenderus.ui.screens.message.MatchList
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileScreen
import com.hcmus.tenderus.ui.viewmodels.MatchListVM


val matchListVM = MatchListVM()

@Composable
fun MainScreen(navController: NavController) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(mainNavController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Logo at the top of the screen
            Image(
                painter = painterResource(id = R.drawable.logo_mainscreen), // Replace with your logo resource
                contentDescription = "Main Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp) // Add padding as needed
                    .size(30.dp) // Adjust size as needed
            )
            // Main content (NavHost)
            Box(
            ) {
                NavHost(
                    navController = mainNavController,
                    startDestination = BottomNavItem.Discover.route
                ) {
                    composable(BottomNavItem.Discover.route) { DiscoverScreen(navController) }
                    composable(BottomNavItem.Matches.route) { MatchesScreen(navController) }
                    composable(BottomNavItem.Explore.route) { ExploreScreen(navController) }
//                    composable(BottomNavItem.Chat.route) { MatchList(matchListVM = matchListVM)}
                    composable(BottomNavItem.Chat.route) { MessageScreen(navController)}
                    composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
                }
            }
        }
    }
}
