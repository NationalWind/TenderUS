package com.hcmus.tenderus

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.screens.SplashScreen
import com.hcmus.tenderus.ui.screens.OnboardingScreen1
//import com.hcmus.tenderus.ui.screens.OnboardingScreen2
//import com.hcmus.tenderus.ui.screens.OnboardingScreen3

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.hcmus.tenderus.utils.firebase.FirebaseEmailAuth
import com.hcmus.tenderus.utils.firebase.FirebaseSMSAuth
import com.hcmus.tenderus.model.User
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "MainAct"
    private lateinit var firebaseSMSAuth: FirebaseSMSAuth
    private lateinit var firebaseEmailAuth: FirebaseEmailAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = Firebase.auth
        // Input this var in every composable that needs to call Firebase services (sendSMS, confirmAndSync)
        firebaseSMSAuth = FirebaseSMSAuth(auth, this)
        firebaseEmailAuth = FirebaseEmailAuth(auth, this)
        Log.d(TAG, "Init")


        enableEdgeToEdge()
        setContent {
            TenderUSTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "splash") {
                    composable("splash") { SplashScreen(navController = navController) }
                    composable("onboarding1") { OnboardingScreen1(navController = navController) }
//                    composable("emailsend") { ExampleEmailSend(firebaseEmailAuth, navController = navController) }
//                    composable("emailsync") { ExampleEmailSync(firebaseEmailAuth) }
//                    composable("onboarding2") { OnboardingScreen2(navController = navController) }
//                    composable("onboarding3") { OnboardingScreen3(navController = navController) }
                }
            }
        }
    }
}

@Composable
fun ExampleEmailSend(firebaseEmailAuth: FirebaseEmailAuth, navController: NavController) {
    var user by remember { mutableStateOf(User("tenten", "toleron", "ng.nguynv@gmail.com", "ahohe")) }
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            try {
                firebaseEmailAuth.sendEmail(user.email)
                navController.navigate("emailsync")
            } catch (e: Exception) {
                Log.d("EmailSend", e.toString())
            }

        }
    }) {
        Text("Send Email")
    }
}
@Composable
fun ExampleEmailSync(firebaseEmailAuth: FirebaseEmailAuth) {
    var user by remember { mutableStateOf(User("tenten", "toleron", "ng.nguynv@gmail.com")) }
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("Sync Email") }

    Text(text)
    LaunchedEffect(Unit) {
        try {
            firebaseEmailAuth.confirmAndSync(user, "RESET_PASSWORD")
            text = "Reset thy password Successfully!!!"
        } catch (e: Exception) {
            Log.d("EmailSync", e.toString())
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