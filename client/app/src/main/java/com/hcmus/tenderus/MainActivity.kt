package com.hcmus.tenderus

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener

import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.ui.screens.SplashScreen
import com.hcmus.tenderus.ui.screens.OnboardingScreen1


import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessaging
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.utils.firebase.FirebaseEmailAuth
import com.hcmus.tenderus.utils.firebase.FirebaseSMSAuth
import com.hcmus.tenderus.model.UserRegistration
import com.hcmus.tenderus.network.ApiClient.LoginApi
import com.hcmus.tenderus.network.ApiClient.ProcessProfile
import com.hcmus.tenderus.network.ApiClient.SyncSignUpApi
import com.hcmus.tenderus.network.SyncSignUp
import com.hcmus.tenderus.ui.screens.MainScreen
import com.hcmus.tenderus.ui.screens.admin.AdminScreen
import com.hcmus.tenderus.ui.screens.authentication.ForgotPasswordScreen

import com.hcmus.tenderus.utils.firebase.TenderUSPushNotificationService
import com.hcmus.tenderus.ui.screens.authentication.LoginScreen
import com.hcmus.tenderus.ui.screens.authentication.SignUpScreen
import com.hcmus.tenderus.ui.screens.discover.DiscoverScreen
import com.hcmus.tenderus.ui.screens.explore.ExploreScreen

import com.hcmus.tenderus.ui.screens.explore.coffe.CateScreen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails1Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails2Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails3Screen
import com.hcmus.tenderus.ui.screens.profilesetup.ProfileDetails4Screen
import com.hcmus.tenderus.ui.screens.profilesetup.SearchPreferencesScreen
import com.hcmus.tenderus.ui.screens.profilesetup.SelectYourGoalsScreen
import com.hcmus.tenderus.ui.screens.message.InChatScreen
import com.hcmus.tenderus.ui.screens.message.MatchList
import com.hcmus.tenderus.ui.screens.profilesetup.HouseRulesScreen
import com.hcmus.tenderus.ui.viewmodels.MatchListVM
import com.hcmus.tenderus.utils.firebase.GenAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    private val TAG = "MainAct"
    private lateinit var firebaseSMSAuth: FirebaseSMSAuth
    private lateinit var firebaseEmailAuth: FirebaseEmailAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
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
    private val REQUEST_CAMERA_PERMISSION = 100
    private val REQUEST_LOCATION_PERMISSION = 101

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            // Permission is already granted, you can use location services
            // TODO: Use location services
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
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

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        TokenManager.init(this)
        requestCameraPermission()
        requestLocationPermission()
        // Input this var in every composable that needs to call Firebase services (sendSMS, confirmAndSync)
        firebaseSMSAuth = FirebaseSMSAuth(this)
        firebaseEmailAuth = FirebaseEmailAuth(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize FCM
        TenderUSPushNotificationService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            TenderUSPushNotificationService.token = task.result
//            Log.d(TAG, "Token: ${task.result}")
        })
//        Log.d(TAG, "Init")

        enableEdgeToEdge()
        setContent {
            TenderUSTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "main") {
//                NavHost(navController, startDestination = "messages") {
                    composable("messages") { MatchList(navController = navController) }
//                    composable("inchat") { InChatScreen(navController = navController, matchListVM = matchListVM)}
                    composable("splash") { SplashScreen(navController = navController) }
                    composable("onboarding1") { OnboardingScreen1(navController = navController) }
//                    composable("signin") { LoginScreen(navController = navController) }
                    composable("signup1") { SignUpScreen(navController, firebaseSMSAuth, firebaseEmailAuth) }
                    composable("profilesetup1") { ProfileDetails1Screen(navController, fusedLocationClient) }
                    composable("profilesetup2") { ProfileDetails2Screen(navController ) }
                    composable("profilesetup3") { ProfileDetails3Screen(navController ) }
                    composable("filter") { SearchPreferencesScreen(navController) }
                    composable("selGoal") { SelectYourGoalsScreen(navController) }
                    composable("add_photos") { ProfileDetails4Screen(navController) }
                    composable("fgpass1") { ForgotPasswordScreen(navController) }
//                    composable("houserules") { HouseRulesScreen(navController) }
//                    composable("main") { MainScreen(navController) }
                    composable("emailsend") { ExampleEmailSend(firebaseEmailAuth, navController = navController) }
                    composable("emailconfirm") { ExampleEmailConfirm(firebaseEmailAuth, navController = navController) }
                    composable("emailsync") { ExampleEmailSync() }
//                    composable("smssend") { ExampleSMSSend(firebaseSMSAuth , navController = navController)}
//                    composable("otpVerification") { OTPVerificationScreen(firebaseSMSAuth , navController = navController) }
                    composable("main") { MainScreen(firebaseSMSAuth, firebaseEmailAuth, applicationContext, fusedLocationClient) }

//                    composable("emailsend") { ExampleEmailSend(firebaseEmailAuth, navController = navController) }
//                    composable("emailsync") { ExampleEmailSync(firebaseEmailAuth) }
                    composable("exlogin") { ExampleLogin(navController) }
                    composable("admin") {AdminScreen()}

                    composable("explore") { ExploreScreen(navController, fusedLocationProviderClient = fusedLocationClient) }
//                    composable("coffee_date") { CoffeeDateScreen(navController) }
                    composable("discover?customTitle={customTitle}") { backStackEntry ->
                        val customTitle = backStackEntry.arguments?.getString("customTitle")
                        DiscoverScreen(navController = navController, customTitle = customTitle,
                            fusedLocationProviderClient = fusedLocationClient)
                    }
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
//                Log.d("Login", e.toString())
            }
        }
    }) {
        Text("Luugin")
    }
}
@Composable
fun ExampleEmailSend(firebaseEmailAuth: FirebaseEmailAuth, navController: NavController) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = {
            scope.launch {
                try {
                    firebaseEmailAuth.sendEmail("maiamtinhthuong8@gmail.com")
                    navController.navigate("emailconfirm")
                } catch (e: Exception) {
//                    Log.d("EmailSend", e.toString())
                }
            }
        }) {
            Text("Send Email")
        }
    }
}


@Composable
fun ExampleEmailConfirm(firebaseEmailAuth: FirebaseEmailAuth, navController: NavController) {
    var userRegistration by remember { mutableStateOf(UserRegistration("tqp912", "nationalwind", "phongtranquoc9124@gmail.com")) }
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("Confirm Email") }

    Text(text)
    LaunchedEffect(Unit) {
        try {
            firebaseEmailAuth.confirm()
            navController.navigate("emailsync")
        } catch (e: Exception) {
//            Log.d("emailConfirm", e.toString())
        }
    }
}

@Composable
fun ExampleEmailSync() {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("Sync Email") }

    Text(text)
    LaunchedEffect(Unit) {
        try {
            GenAuth.syncForSignUp("abcdxyz", "abcdxyz")
            text = "Sign up Successfully!!!"
        } catch (e: Exception) {
//            Log.d("EmailSync", e.toString())
        }
    }
}

//@Composable
//fun ExampleSMSSend(firebaseSMSAuth: FirebaseSMSAuth, navController: NavController) {
//    var userRegistration by remember { mutableStateOf(UserRegistration("tenten", "toleron", "ng.nguynv@gmail.com", "ahohe")) }
//    var phoneNumber by remember { mutableStateOf("+84772405038") } // Replace with the user's phone number
//    val scope = rememberCoroutineScope()
//
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Button(onClick = {
//            scope.launch {
//                try {
//                    firebaseSMSAuth.sendSMS(phoneNumber)
//                    // Navigate to OTP verification screen
//                    navController.navigate("otpVerification")
//                } catch (e: Exception) {
//                    Log.d("SMSSend", e.toString())
//                }
//            }
//        }) {
//            Text("Send SMS")
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OTPVerificationScreen(
//    firebaseSMSAuth: FirebaseSMSAuth,
//    navController: NavController
//) {
//    var otp by remember { mutableStateOf("") }
//    val scope = rememberCoroutineScope()
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//    var userRegistration by remember { mutableStateOf(UserRegistration("tqp912", "nationalwind", "phongtranquoc9@gmail.com", "+84772405038")) }
//    var syncFor by remember { mutableStateOf("SIGN_UP") } // Replace with the user's phone number
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Enter the OTP sent to your phone", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (otp.isEmpty()) OutlinedTextField(
//            value = otp,
//            onValueChange = { otp = it },
//            label = { Text("OTP") },
//            visualTransformation = VisualTransformation.None,
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        ) else OutlinedTextField(
//            value = otp,
//            onValueChange = { otp = it },
//            label = { Text("OTP") },
//            visualTransformation = PasswordVisualTransformation(),
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Button(onClick = {
//            scope.launch {
//                try {
//                    firebaseSMSAuth.confirmAndSync(userRegistration, otp, syncFor)
//                    navController.navigate("home") // Navigate to the home screen or any other screen upon success
//                } catch (e: Exception) {
//                    errorMessage = e.localizedMessage
//                    Log.e("OTPVerification", "Error verifying OTP", e)
//                }
//            }
//        }) {
//            Text("Verify OTP")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        errorMessage?.let {
//            Text(text = it, color = MaterialTheme.colorScheme.error)
//        }
//    }
//}

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
