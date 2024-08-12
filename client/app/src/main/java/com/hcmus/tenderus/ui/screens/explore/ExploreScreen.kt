package com.hcmus.tenderus.ui.screens.explore

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hcmus.tenderus.R

@Composable
fun ExploreScreen(navController: NavController) {
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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WelcomeSection()
                CategorySection()
                WelcomeJointopic()
                TopicSection()
            }
        }
    }
}

@Composable
fun WelcomeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp),

    ) {
        Text(
            text = "Welcome to Explore",
            color = Color(0xFFBD0D36),
            fontSize = 27.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "My Vibe ...",
            color = Color(0xFF979797),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun CategorySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center the images horizontally
        ) {
            CategoryItem(
                imageRes = R.drawable.looking_for_love,
                text = "Looking for\nLove"
            )
            Spacer(modifier = Modifier.width(16.dp)) // Increase space between items
            CategoryItem(
                imageRes = R.drawable.free_tonight,
                text = "Free \ntonight?"
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Increase space between rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center the images horizontally
        ) {
            CategoryItem(
                imageRes = R.drawable.coffe_date,
                text = "Coffee\nDate"
            )
            Spacer(modifier = Modifier.width(16.dp)) // Increase space between items
            CategoryItem(
                imageRes = R.drawable.let_friend,
                text = "Let's be \nfriend"
            )
        }
    }
}

@Composable
fun WelcomeJointopic() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 5.dp),
        
    ) {
        Text(
            text = "For you",
            color = Color(0xFFBD0D36),
            fontSize = 27.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "Suggested based on your profile",
            color = Color(0xFF979797),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun TopicSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center the images horizontally
        ) {
            CategoryItem(
                imageRes = R.drawable.drink,
                text = "Like to go \ndrinking"
            )
            Spacer(modifier = Modifier.width(16.dp)) // Increase space between items
            CategoryItem(
                imageRes = R.drawable.movie,
                text = "Movie \nLovers"
            )
        }
        Spacer(modifier = Modifier.height(16.dp)) // Increase space between rows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center // Center the images horizontally
        ) {
            CategoryItem(
                imageRes = R.drawable.creative,
                text = "Creative \nLovers"
            )
            Spacer(modifier = Modifier.width(16.dp)) // Increase space between items
            CategoryItem(
                imageRes = R.drawable.sport,
                text = "Love \nSports"
            )
        }
    }
}

@Composable
fun CategoryItem(imageRes: Int, text: String) {
    Box(
        modifier = Modifier
            .width(180.dp) // Increase the width of the item
            .aspectRatio(0.75f) // Adjust the aspect ratio for larger images
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (text.isNotEmpty()) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(8.dp)
            )
        }
    }
}
