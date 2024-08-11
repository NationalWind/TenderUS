package com.hcmus.tenderus.ui.screens

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.hcmus.tenderus.ui.screens.message.InChatScreen
import com.hcmus.tenderus.ui.screens.message.MatchList
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileScreen
import com.hcmus.tenderus.ui.viewmodels.MatchListVM


val matchListVM = MatchListVM()

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun MainScreen(/*navController: NavController*/) {
    val mainNavController = rememberNavController()
    var showBar by remember { mutableStateOf(true) }
    Scaffold(

        bottomBar = {

            if (showBar) BottomNavigationBar(mainNavController)

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
        ) {
            // Logo at the top of the screen
            if (showBar) {
                Image(
                    painter = painterResource(id = R.drawable.logo_mainscreen), // Replace with your logo resource
                    contentDescription = "Main Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp) // Add padding as needed
                        .size(30.dp) // Adjust size as needed
                )
            }

            // Main content (NavHost)
            Box(
            ) {
                NavHost(
                    navController = mainNavController,
                    startDestination = BottomNavItem.Discover.route,

                ) {
                    composable(BottomNavItem.Discover.route) {
                        LaunchedEffect(Unit) {
                            showBar = true
                        }
                        DiscoverScreen(mainNavController/*navController*/)
                    }
                    composable(BottomNavItem.Matches.route) {
                        LaunchedEffect(Unit) {
                            showBar = true
                        }
                        MatchesScreen(mainNavController/*navController*/)
                    }
                    composable(BottomNavItem.Explore.route) {
                        LaunchedEffect(Unit) {
                            showBar = true
                        }
                        ExploreScreen(mainNavController/*navController*/)
                    }
                    composable(BottomNavItem.Chat.route) {
                        LaunchedEffect(Unit) {
                            showBar = true
                        }
                        MatchList(mainNavController, matchListVM = matchListVM)
                    }
                    composable("inchat") {
                        LaunchedEffect(Unit) {
                            showBar = false
                        }
                        InChatScreen(navController = mainNavController, matchListVM = matchListVM)
                    }
//                    composable(BottomNavItem.Chat.route) { MessageScreen(navController)}
                    composable(BottomNavItem.Profile.route) {
                        ProfileScreen(mainNavController/*navController*/)
                    }
                }
            }
        }
    }
}


