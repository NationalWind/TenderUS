package com.hcmus.tenderus.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.hcmus.tenderus.R
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.network.ApiClient.ProcessProfile
import com.hcmus.tenderus.ui.screens.admin.AdminScreen
import com.hcmus.tenderus.ui.screens.authentication.ForgotPasswordScreen
import com.hcmus.tenderus.ui.screens.authentication.LoginScreen
import com.hcmus.tenderus.ui.screens.authentication.SignUpScreen
import com.hcmus.tenderus.ui.screens.discover.DiscoverScreen
import com.hcmus.tenderus.ui.screens.discover.ItsAMatchScreen
import com.hcmus.tenderus.ui.screens.discover.MatchesScreen
import com.hcmus.tenderus.ui.screens.explore.ExploreScreen
import com.hcmus.tenderus.ui.screens.explore.FriendScreen
import com.hcmus.tenderus.ui.screens.explore.coffe.CoffeeDateScreen

import com.hcmus.tenderus.ui.screens.message.InChatScreen
import com.hcmus.tenderus.ui.screens.message.MatchList
import com.hcmus.tenderus.ui.screens.profilesetup.Add_Photos
import com.hcmus.tenderus.ui.screens.profilesetup.EditProfileScreen
import com.hcmus.tenderus.ui.screens.profilesetup.HouseRulesScreen
import com.hcmus.tenderus.ui.screens.profilesetup.Interest
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails1Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails2Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails3Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails4Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileScreen
import com.hcmus.tenderus.ui.screens.profilesetup.SearchPreferencesScreen
import com.hcmus.tenderus.ui.screens.profilesetup.SelectYourGoalsScreen
import com.hcmus.tenderus.ui.viewmodels.ExploreVM
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.ui.viewmodels.ProfileVM
import com.hcmus.tenderus.utils.ActivityStatusService
import com.hcmus.tenderus.utils.firebase.FirebaseEmailAuth
import com.hcmus.tenderus.utils.firebase.FirebaseSMSAuth


@SuppressLint("UnrememberedGetBackStackEntry")
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun MainScreen(firebaseSMSAuth: FirebaseSMSAuth, firebaseEmailAuth: FirebaseEmailAuth, context: Context, fusedLocationClient: FusedLocationProviderClient) {
    var isLoggedIn by remember {
        mutableStateOf(TokenManager.getToken() != null)
    }
    var firstTime by remember {
        mutableStateOf(TokenManager.getFirstTime())
    }
    var isAdmin by remember {
        mutableStateOf(TokenManager.getRole() == "ADMIN")
    }

    if (!isLoggedIn) {
        val mainNavController = rememberNavController()
        NavHost(
            navController = mainNavController,
            startDestination = "signin"
        ) {
            composable("signin") {
                LoginScreen(navController = mainNavController) {
                    isLoggedIn = true
                    firstTime = it.firstTime
                    isAdmin = it.role == "ADMIN"
                }
            }
            composable("signup1") {
                SignUpScreen(mainNavController, firebaseSMSAuth, firebaseEmailAuth)
            }
        }

    } else {
        if (isAdmin) {
            AdminScreen {
                isLoggedIn = false
            }
        } else {
            val mainNavController = rememberNavController()
            var showBar by remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                try {
                    TokenManager.getToken()?.let {token ->
                        if (token != "") {
                            ProcessProfile.upsertUserProfile(
                                "Bearer $token",
                                profile = Profile(isActive = true)
                            )
                            val intent = Intent(context, ActivityStatusService::class.java)
                            context.startService(intent)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Profile", "Activity Status Update Failed")
                }

            }
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
                                //                        .padding(top = 1.dp) // Add padding as needed
                                .size(30.dp) // Adjust size as needed
                        )
                    }
                    Log.d("dsoiegh", TokenManager.getToken() ?: "")
                    // Main content (NavHost)
                    NavHost(
                        navController = mainNavController,
                        startDestination = if (firstTime) "profilesetup1" else BottomNavItem.Discover.route

                    ) {

                        composable(BottomNavItem.Discover.route) {
                            LaunchedEffect(Unit) {
                                showBar = true
                            }
                            DiscoverScreen(mainNavController/*navController*/,"Discover")
                        }
                        composable("itsamatch") {
                            LaunchedEffect(Unit) {
                                showBar = true
                            }
                            ItsAMatchScreen(mainNavController/*navController*/)
                        }

                        composable(BottomNavItem.Explore.route) {
                            LaunchedEffect(Unit) {
                                showBar = true
                            }
                            ExploreScreen(mainNavController/*navController*/)
                        }
                        composable("coffee_date") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            val backStackEntry =
                                remember { mainNavController.getBackStackEntry(BottomNavItem.Explore.route) }
                            CoffeeDateScreen(mainNavController, viewModel(viewModelStoreOwner = backStackEntry, factory = ExploreVM.Factory))
                        }
                        composable("let's be friend") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            val backStackEntry =
                                remember { mainNavController.getBackStackEntry(BottomNavItem.Explore.route) }
                            FriendScreen(mainNavController, viewModel(viewModelStoreOwner = backStackEntry, factory = ExploreVM.Factory))
                        }
                        composable(BottomNavItem.Chat.route) {
                            LaunchedEffect(Unit) {
                                showBar = true
                            }
                            MatchList(mainNavController)
                        }
                        composable("inchat") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            val loginBackStackEntry =
                                remember { mainNavController.getBackStackEntry(BottomNavItem.Chat.route) }
                            InChatScreen(
                                navController = mainNavController,
                                context,
                                viewModel(loginBackStackEntry)
                            )
                        }
                        //                    composable(BottomNavItem.Chat.route) { MessageScreen(navController)}
                        composable(BottomNavItem.Profile.route) {
                            LaunchedEffect(Unit) {
                                showBar = true
                            }
                            ProfileScreen(mainNavController) {
                                isLoggedIn = false
                            }
                        }



                        composable("profilesetup1") {
                            LaunchedEffect(Unit) {
                                showBar = false
                                Log.d("firstTime", firstTime.toString())
                            }
                            ProfileDetails1Screen(mainNavController, fusedLocationClient)

                        }
                        composable("profilesetup2") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            ProfileDetails2Screen(mainNavController)
                        }
                        composable("profilesetup3") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            ProfileDetails3Screen(mainNavController)
                        }

                        composable("editprofile") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            EditProfileScreen(mainNavController)
                        }
                        composable("interest") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            Interest(mainNavController)
                        }

                        composable("about_us") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            AboutUsScreen(mainNavController)
                        }

                        composable("addphoto") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            Add_Photos(mainNavController)
                        }

                        //                    composable("main") {
                        //                        MainScreen(firebaseSMSAuth, firebaseEmailAuth, matchListVM, context)
                        //                    }
                        composable("admin") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            AdminScreen()
                        }

                        composable("filter") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            SearchPreferencesScreen(mainNavController)
                        }
                        composable("selGoal") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            SelectYourGoalsScreen(mainNavController)
                        }
                        composable("add_photos") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            ProfileDetails4Screen(mainNavController)
                        }
                        composable("fgpass1") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            ForgotPasswordScreen(mainNavController)
                        }
                        composable("houserules") {
                            LaunchedEffect(Unit) {
                                showBar = false
                            }
                            HouseRulesScreen(mainNavController) {
                                TokenManager.saveFirstTime(false)
                                firstTime = false
                            }
                        }
//                        composable("main") {
//                            LaunchedEffect(Unit) {
//                                showBar = true
//                            }
//                            MainScreen(firebaseSMSAuth, firebaseEmailAuth, context)
//                        }
                    }

                }
            }
        }
    }
}


