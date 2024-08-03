package com.hcmus.tenderus.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.ui.screens.discover.ChatScreen
import com.hcmus.tenderus.ui.screens.discover.DiscoverScreen
import com.hcmus.tenderus.ui.screens.discover.ExploreScreen
import com.hcmus.tenderus.ui.screens.discover.MatchesScreen
import com.hcmus.tenderus.ui.screens.discover.ProfileScreen

@Composable
fun MainScreen(navController: NavController) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(mainNavController) }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = BottomNavItem.Discover.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Discover.route) { DiscoverScreen(navController) }
            composable(BottomNavItem.Matches.route) { MatchesScreen(navController) }
            composable(BottomNavItem.Explore.route) { ExploreScreen(navController) }
            composable(BottomNavItem.Chat.route) { ChatScreen(navController) }
            composable(BottomNavItem.Profile.route) { ProfileScreen(navController) }
        }
    }
}
