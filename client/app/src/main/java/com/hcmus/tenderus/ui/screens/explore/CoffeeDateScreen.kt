package com.hcmus.tenderus.ui.screens.explore.coffe

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

@Composable
fun CoffeeDateScreen(navController: NavController) {
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
                CoffeeImage()
                Spacer(modifier = Modifier.height(20.dp))
                Title()
                Spacer(modifier = Modifier.height(8.dp))
                Subtitle()
                Spacer(modifier = Modifier.height(54.dp))
                JoinAndBackButtons(navController)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun JoinAndBackButtons(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = { navController.navigate("discover?customTitle=Coffe Date") },
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
            onClick = { navController.popBackStack()  },

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
private fun CoffeeImage() {
    Image(
        painter = painterResource(id = R.drawable.coffe_icon),
        contentDescription = "Coffee cup image",
        modifier = Modifier
            .size(271.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun Title() {
    Text(
        text = "Go for coffee",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFBD0D36),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun Subtitle() {
    Text(
        text = "Find someone to go for coffee with",
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF828693),
        letterSpacing = 0.4.sp
    )
}

@Composable
private fun JoinButton(navController: NavController) {
    Button(
        onClick = {navController.navigate("discover?customTitle=Coffe Date")},
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

    // Nút No, thank you
    TextButton(onClick = {
        // Quay trở về màn hình trước đó
        navController.popBackStack()
    }) {
        Text(
            text = "No, thank you",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFABABAB),
            textAlign = TextAlign.Center
        )
    }
}




@Composable
fun CoffeeDate(navController: NavController) {
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ảnh tách cà phê
                Image(
                    painter = painterResource(id = R.drawable.coffe_icon),
                    contentDescription = "Coffee Image",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Văn bản Go for coffee
                Text(
                    text = "Go for coffee",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFFD32F2F)
                )

                // Văn bản Find someone to go for coffee with
                Text(
                    text = "Find someone to go for coffee with",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Nút Join now
                Button(
                    onClick = {
                        // Điều hướng đến DiscoverScreen
                        navController.navigate("discover?customTitle=Coffe Date")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(text = "Join now", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Nút No, thank you
                TextButton(onClick = {
                    // Quay trở về màn hình trước đó
                    navController.popBackStack()
                }) {
                    Text(text = "No, thank you", color = Color.Gray)
                }
            }
        }

    )
}

