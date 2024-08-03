package com.hcmus.tenderus.ui.screens

import com.hcmus.tenderus.R

sealed class BottomNavItem(val route: String, val icon: Int, val title: String) {
    object Discover : BottomNavItem("discover", R.drawable.discover_ic, "Discover")
    object Matches : BottomNavItem("matches", R.drawable.matches_ic, "Matches")
    object Explore : BottomNavItem("explore", R.drawable.explore_ic, "Explore")
    object Chat : BottomNavItem("chat", R.drawable.chat_ic, "Chat")
    object Message : BottomNavItem("profile", R.drawable.profile_ic, "Profile")
}
