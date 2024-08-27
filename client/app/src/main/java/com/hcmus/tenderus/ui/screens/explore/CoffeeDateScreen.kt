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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hcmus.tenderus.ui.viewmodels.ExploreVM

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun CoffeeDateScreen(navController: NavController, exploreVM: ExploreVM) {
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
                JoinAndBackButtons(navController, exploreVM)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
private fun JoinAndBackButtons(navController: NavController, exploreVM: ExploreVM) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            onClick = {
                exploreVM.join("Coffee Date")
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



