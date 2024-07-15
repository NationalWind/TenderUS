package com.hcmus.tenderus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.SplashScreen
import com.hcmus.tenderus.ui.OnboardingScreen1
//import com.hcmus.tenderus.ui.OnboardingScreen2
//import com.hcmus.tenderus.ui.OnboardingScreen3

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TenderUSTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController = navController) }
                    composable("onboarding1") { OnboardingScreen1(navController = navController) }
//                    composable("onboarding2") { OnboardingScreen2(navController = navController) }
//                    composable("onboarding3") { OnboardingScreen3(navController = navController) }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TenderUSTheme {
        Greeting("Android")
    }
}
