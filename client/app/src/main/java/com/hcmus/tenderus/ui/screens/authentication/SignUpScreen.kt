package com.hcmus.tenderus.ui.screens.authentication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hcmus.tenderus.R
import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.model.UserRegistration
import com.hcmus.tenderus.network.ApiClient.SyncSignUpApi
import com.hcmus.tenderus.network.SyncSignUp
import com.hcmus.tenderus.utils.firebase.FirebaseEmailAuth
import com.hcmus.tenderus.utils.firebase.FirebaseSMSAuth
import com.hcmus.tenderus.utils.firebase.GenAuth
import com.hcmus.tenderus.utils.firebase.TenderUSPushNotificationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    firebaseSMSAuth: FirebaseSMSAuth,
    firebaseEmailAuth: FirebaseEmailAuth
) {
    var step by remember { mutableStateOf(1) }
    var selectedCountryCode by remember { mutableStateOf("+84") }
    var phoneNumber by remember { mutableStateOf("") }
    var fullPhoneNumber by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var verificationCode by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(120) }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var isUsernameValid by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var isPasswordValid by remember { mutableStateOf(true) }
    var doPasswordsMatch by remember { mutableStateOf(true) }
    var isSignUpSuccessful by remember { mutableStateOf(false) }
    var isSendingSMS by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf("") }
    var isResendAllowed by remember { mutableStateOf(true) }
    var progress by remember { mutableStateOf(0f) } // Progress value
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    // Country codes data
    val countryCodes = listOf("+84","+1", "+44", "+33", "+49", "+34", "+39", "+81", "+82", "+86", "+91", "+61", "+55", "+7", "+27", "+30", "+31", "+32", "+41", "+43", "+46", "+47", "+48", "+60", "+63", "+64", "+65", "+66", "+70", "+71", "+72", "+73", "+74", "+75", "+76", "+77", "+78", "+79", "+80", "+81", "+82", "+83")
    var expanded by remember { mutableStateOf(false) }

    // Handle timer countdown for verification code
    LaunchedEffect(step) {
        if (step == 2) {
            timer = 120
            while (timer > 0) {
                delay(1000L)
                timer--
            }
        }
    }

    // Reset error and success messages on step change
    LaunchedEffect(step) {
        errorMessage = null
        successMessage = ""
    }

    // Function to combine country code and phone number
    fun formatPhoneNumber(): String {
        return "$selectedCountryCode$phoneNumber"
    }

    // Function to validate phone number
    fun isPhoneNumberValid(phone: String): Boolean {
        return phone.matches("^\\+[1-9]\\d{1,14}\$".toRegex())
    }

    // Function to resend the verification code
    fun resendCode() {
        if (isResendAllowed) {
            isResendAllowed = false
            scope.launch {
                try {
                    firebaseSMSAuth.sendSMS(phoneNumber)
                    timer = 120 // Reset timer
                } catch (e: Exception) {
                    errorMessage = "Failed to resend SMS: ${e.message}"
                }
            }
        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            val image: Painter = painterResource(id = R.drawable.tim)
            Image(
                painter = image,
                contentDescription = "Heart Icon",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            when (step) {
                1 -> {
                    // Step 1: Phone Number Input
                    Text(
                        "Can you share your phone number?",
                        fontSize = 30.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Country Code Dropdown
                        Box(
                            modifier = Modifier
//                                .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                                .clickable { expanded = !expanded }
                                .padding(16.dp)
                        ) {
                            Text(text = selectedCountryCode, fontSize = 18.sp, color = Color(0xFFB71C1C))
                            Spacer(modifier = Modifier.width(57.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.drdown),
                                contentDescription = "Dropdown",
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .size(20.dp),
                                tint = Color(0xFFB71C1C)
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                countryCodes.forEach { code ->
                                    DropdownMenuItem(
                                        text = { Text(text = code) },
                                        onClick = {
                                            selectedCountryCode = code
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        // Phone Number Input Field
                        TextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            placeholder = { Text("Phone Number") },
                            modifier = Modifier
                                .weight(1f), // Take up the remaining space
                            keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() },
                            )
                        )
                    }

                    if (!isPhoneNumberValid) {
                        Text(
                            "Invalid phone number",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "We will send you a message with a verification code to confirm it's you. You may incur messaging and data charges.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            fullPhoneNumber = formatPhoneNumber()
                            if (isPhoneNumberValid(fullPhoneNumber) && !isSendingSMS) {
                                isSendingSMS = true
                                scope.launch {
                                    try {
                                        Log.d("SignUp", "Sending SMS to $fullPhoneNumber")
                                        firebaseSMSAuth.sendSMS(fullPhoneNumber)
                                        step = 2
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to send SMS: ${e.message}"
                                        Log.e("SignUp", "SMS send error", e)
                                    } finally {
                                        isSendingSMS = false
                                    }
                                }
                            } else {
                                isPhoneNumberValid = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue", color = Color.White)
                    }
                    Text(
                        text = "or Sign Up with your email",
                        fontSize = 14.sp,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            step = 3
                        }
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already have an account?",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign In",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("signin") // Navigate to Sign In screen
                            }
                        )
                    }


                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }
                }
                2 -> {
                    // Step 2: Verification Code Input
                    Text(
                        "Enter Verification Code",
                        fontSize = 30.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = String.format("%02d:%02d", timer / 60, timer % 60),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BasicTextField(
                        value = verificationCode,
                        onValueChange = { verificationCode = it },
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "If you didn't receive a code,",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        "Resend",
                        fontSize = 14.sp,
                        color = Color.Blue,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                resendCode()
                            }
                            .padding(8.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = {
                            // Here, you would typically validate the verification code
                            scope.launch {
                                try {
                                    firebaseSMSAuth.confirm(verificationCode)
                                    step = 5
                                } catch (e: Exception) {
                                    Log.d("SMSSend", e.toString())
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SEND", color = Color.White)
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already have an account?",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign In",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("signin") // Navigate to Sign In screen
                            }
                        )
                    }
                }
                3 -> {
                    // Step 3: Email Input
                    Text(
                        "Enter your email address",
                        fontSize = 30.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BasicTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            isEmailValid = true
                        },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        )
                    )
                    if (!isEmailValid) {
                        Text(
                            "Invalid email address",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (isEmailValid) {
                                scope.launch {
                                    try {
                                        firebaseEmailAuth.sendEmail(email)
                                        step = 4
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to send email: ${e.message}"
                                        Log.d("EmailSend", e.toString())
                                    }
                                }
                            } else {
                                isEmailValid = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Continue", color = Color.White)
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already have an account?",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign In",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("signin") // Navigate to Sign In screen
                            }
                        )
                    }
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }
                }
                4 -> {
                    // Step 4: Waiting for Email Verification
                    LaunchedEffect(Unit) {
                        scope.launch {
                            try {
                                firebaseEmailAuth.confirm()
                                step = 5 // Proceed to the next screen after verification

                            } catch (e: Exception) {
                                errorMessage = "Failed to check verification status: ${e.message}"
                            }
                        }
                    }
                    Text(
                        "Verify your email address",
                        fontSize = 30.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "A verification link has been sent to your email. Please check your inbox and click the link to verify your email.",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                        onClick = {}
                    ) {
                        Text("Checking Verification Status...", color = Color.White)
                    }
                    errorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }
                }

                5 -> {
                    // Step 5: Password & Username Creation
                    Text(
                        "Enter a username",
                        fontSize = 30.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
//                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            isUsernameValid = validateUsername(username)
                        },
                        label = { Text("Username") },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
//                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
//                            .padding(16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        )
                    )
                    if (!isUsernameValid) {
                        Text(
                            "Invalid username",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Create a password",
                        fontSize = 30.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
//                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            isPasswordValid = validatePassword(password.text)
                        },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
//                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
//                            .padding(16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        )
                    )
                    if (!isPasswordValid) {
                        Text(
                            "Password must be at least 8 characters",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
//                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            doPasswordsMatch = password.text == confirmPassword.text
                        },
                        label = { Text("Confirm Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
//                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
//                            .padding(16.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        )
                    )
                    if (!doPasswordsMatch) {
                        Text(
                            "Passwords do not match",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            if (isPasswordValid && doPasswordsMatch) {
                                isLoading = true
                                isSuccess = false
                                errorMessage = ""
                                progress = 0.0f
                                scope.launch {
                                    while (progress < 1.0f) {
                                        progress += 0.1f
                                        delay(30) // Simulate loading
                                    }
                                    try {
                                        // Simulate API call
                                        GenAuth.syncForSignUp(username, password.text)
                                        isSuccess = true
//                                        successMessage = "Account created successfully.\n Please log in to set up your profile."
                                        delay(20)
                                        navController.navigate("signin")
                                    } catch (e: retrofit2.HttpException) {
                                        Log.d("Signup", e.toString())
                                        errorMessage = "Internal server error. This could be due to a duplicate username or other server issues. Please try again later."
                                    } catch (e: Exception) {
                                        errorMessage = "An unexpected error occurred"
                                        Log.d("Signup", e.toString())
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                            Text("Create an account", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    errorMessage?.let {
                        Text(text = it, color = Color.Red, fontSize = 14.sp)
                    }


                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Already have an account?",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sign In",
                            fontSize = 14.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                navController.navigate("signin") // Navigate to Sign In screen
                            }
                        )

                    }
                }
            }
        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),  // Semi-transparent black overlay
            contentAlignment = Alignment.Center  // Center the CircularProgressIndicator
        ) {
            CircularProgressIndicator(color = Color.White)  // White progress indicator
        }
    }
}

// Function to validate the password
fun validatePassword(password: String): Boolean {
    // Example validation: Check if the password is at least 8 characters long
    return password.length >= 8
}

fun validateUsername(username: String): Boolean {
    return username.isNotEmpty() && username.length >= 3
}

@Composable
fun CircularProgressWithPercent(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(100.dp), // Adjust size as needed
            color = Color(0xFFB71C1C),
            strokeWidth = 8.dp // Adjust stroke width as needed
        )
        Text(
            text = "${(progress * 100).toInt()}%",
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}