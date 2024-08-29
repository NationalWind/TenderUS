package com.hcmus.tenderus.ui.screens.explore.coffe

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hcmus.tenderus.R
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.ui.draw.clip

import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.sp
import com.hcmus.tenderus.ui.viewmodels.ExploreVM

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun CateScreen(navController: NavController, exploreVM: ExploreVM, category: String) {
    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(bottom = 15.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(71.dp))
                Illustration(category)
                Spacer(modifier = Modifier.height(20.dp))
                Title(category)
                Spacer(modifier = Modifier.height(8.dp))
                Subtitle(category)
                Spacer(modifier = Modifier.height(54.dp))
                JoinAndBackButtons(navController, exploreVM, category)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
private fun JoinAndBackButtons(navController: NavController, exploreVM: ExploreVM, category: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                exploreVM.join(category)
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBD0D36)),
            shape = RoundedCornerShape(15.dp)
        ) {
            Text(
                text = "Join now",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.popBackStack() },
        ) {
            Text(
                text = "No, thank you",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFABABAB),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun Illustration(category: String) {
    val imageRes = when (category) {
        "Looking for Love" -> R.drawable.love_icon
        "Free tonight" -> R.drawable.drink_icon
        "Coffee Date" -> R.drawable.coffe_icon
        "Let's be friend" -> R.drawable.friend
        "Like to go drinking" -> R.drawable.drink_icon
        "Movie Lovers" -> R.drawable.free_tonight_icon
        "Creative Lovers" -> R.drawable.creative_icon
        "Love Sports" -> R.drawable.sport_icon
        else -> R.drawable.profile_placeholder
    }
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "$category image",
        modifier = Modifier
            .size(271.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun Title(category: String) {
    val titleText = when (category) {
        "Looking for Love" -> "Looking for Love"
        "Free tonight" -> "Free tonight?"
        "Coffee Date" -> "Go for coffee"
        "Let's be friend" -> "Let's be friends"
        "Like to go drinking" -> "Let's go drinking"
        "Movie Lovers" -> "Movie Lovers"
        "Creative Lovers" -> "Creative Lovers"
        "Love Sports" -> "Love Sports"
        else -> "Explore"
    }
    Text(
        text = titleText,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFBD0D36),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Subtitle(category: String) {
    val subtitleText = when (category) {
        "Looking for Love" -> "Find someone to love"
        "Free tonight" -> "Find someone to hang out with tonight"
        "Coffee Date" -> "Find someone to go for coffee with"
        "Let's be friend" -> "Make new friends"
        "Like to go drinking" -> "Find a drinking buddy"
        "Movie Lovers" -> "Watch a movie together"
        "Creative Lovers" -> "Connect with creative minds"
        "Love Sports" -> "Find a sports partner"
        else -> "Explore the categories"
    }
    Text(
        text = subtitleText,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF828693),
        letterSpacing = 0.4.sp
    )
}