package com.hcmus.tenderus.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.hcmus.tenderus.R

@Composable
fun OnboardingScreen1(navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White)
    ) {
        Text(
            text = "Welcome to ",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Image(
            painter = painterResource(id = R.drawable.logo1_2),
            contentDescription = "logo",
            modifier = Modifier.size(220.dp)
        )
        Text(
            text = "Connect with fellow HCMUS students and discover new friendships and romances.",
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { navController.navigate("onboarding2") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Next")
        }
    }
}

