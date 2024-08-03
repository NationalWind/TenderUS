package com.hcmus.tenderus

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.OnCompleteListener

import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.screens.SplashScreen
import com.hcmus.tenderus.ui.screens.OnboardingScreen1


import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessaging
import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.utils.firebase.FirebaseEmailAuth
import com.hcmus.tenderus.utils.firebase.FirebaseSMSAuth
import com.hcmus.tenderus.model.UserRegistration
import com.hcmus.tenderus.network.ApiClient.LoginApi
import com.hcmus.tenderus.ui.screens.authentication.ForgotPasswordScreen1
import com.hcmus.tenderus.ui.screens.authentication.ForgotPasswordScreen2
import com.hcmus.tenderus.ui.screens.authentication.ForgotPasswordScreen3
import com.hcmus.tenderus.utils.firebase.TenderUSPushNotificationService
import com.hcmus.tenderus.ui.screens.authentication.LoginScreen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails1Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails2Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails3Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails4Screen
import com.hcmus.tenderus.ui.screens.profilesetup.SearchPreferencesScreen
import com.hcmus.tenderus.ui.screens.profilesetup.SelectYourGoalsScreen
import com.hcmus.tenderus.ui.screens.authentication.SignUpScreen1
import com.hcmus.tenderus.ui.screens.authentication.SignUpScreen2
import com.hcmus.tenderus.ui.screens.authentication.SignUpScreen3
import com.hcmus.tenderus.ui.screens.authentication.SignUpScreen4
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val TAG = "MainAct"
    private lateinit var firebaseSMSAuth: FirebaseSMSAuth
    private lateinit var firebaseEmailAuth: FirebaseEmailAuth
    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        val auth = Firebase.auth
        // Input this var in every composable that needs to call Firebase services (sendSMS, confirmAndSync)
        firebaseSMSAuth = FirebaseSMSAuth(auth, this)
        firebaseEmailAuth = FirebaseEmailAuth(auth, this)

        // Initialize FCM
        TenderUSPushNotificationService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            TenderUSPushNotificationService.token = task.result
            Log.d(TAG, "Token: ${task.result}")
        })
        Log.d(TAG, "Init")


        enableEdgeToEdge()
        setContent {
            TenderUSTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "splash") {
//                    composable("splash") { ExampleLogin(navController = navController)}
                    composable("splash") { SplashScreen(navController = navController) }
                    composable("onboarding1") { OnboardingScreen1(navController = navController) }
                    composable("signin") { LoginScreen(navController = navController) }
                    composable("signup1") { SignUpScreen1(navController) }
                    composable("signup2") { SignUpScreen2(navController) }
                    composable("signup3") { SignUpScreen3(navController) }
                    composable("signup4") { SignUpScreen4(navController) }
                    composable("profilesetup1") { ProfileDetails1Screen(navController) }
                    composable("profilesetup2") { ProfileDetails2Screen(navController ) }
                    composable("profilesetup3") { ProfileDetails3Screen(navController ) }  // user preferences
                    composable("filter") { SearchPreferencesScreen(navController) }
                    composable("selGoal") { SelectYourGoalsScreen(navController) }
                    composable("add_photos") { ProfileDetails4Screen(navController) }
                    composable("fgpass1") { ForgotPasswordScreen1(navController) }
                    composable("fgpass2") { ForgotPasswordScreen2(navController)}
                    composable("fgpass3") { ForgotPasswordScreen3(navController) }
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
fun ExampleLogin(navController: NavController) {
    var userLogin by remember { mutableStateOf(UserLogin("tenten", "toleron", "ng.nguynv@gmail.com", "", "cblYShf1QsCfMZaW7TFtZe:APA91bE_MTPlA_n05dUm_Rm3NB9x5i-teVhb6YihL_lGf0SfOotpfalF7pRnUUriyVatw10moHgDLJX_XcY_Ipo0y1S7WaYqWk6HKw91S-bRttpRJb2STNoCZ0hx6g7xKvsBmGIoItmM")) }
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            try {
                LoginApi.login(userLogin)
            } catch (e: Exception) {
                Log.d("Login", e.toString())
            }
        }
    }) {
        Text("Luugin")
    }
}

@Composable
fun ExampleEmailSend(firebaseEmailAuth: FirebaseEmailAuth, navController: NavController) {
    var userRegistration by remember { mutableStateOf(UserRegistration("tenten", "toleron", "ng.nguynv@gmail.com", "ahohe")) }
    val scope = rememberCoroutineScope()
    Button(onClick = {
        scope.launch {
            try {
                firebaseEmailAuth.sendEmail(userRegistration.email)
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
    var userRegistration by remember { mutableStateOf(UserRegistration("tenten", "toleron", "ng.nguynv@gmail.com")) }
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("Sync Email") }

    Text(text)
    LaunchedEffect(Unit) {
        try {
            firebaseEmailAuth.confirmAndSync(userRegistration, "RESET_PASSWORD")
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
