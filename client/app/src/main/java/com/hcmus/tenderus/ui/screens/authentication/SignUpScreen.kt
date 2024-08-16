package com.hcmus.tenderus.ui.screens.authentication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
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
    var phoneNumber by remember { mutableStateOf("") }
    var isPhoneNumberValid by remember { mutableStateOf(true) }
    var verificationCode by remember { mutableStateOf("") }
    var timer by remember { mutableStateOf(60) }
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

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    // Handle timer countdown for verification code
    LaunchedEffect(step) {
        if (step == 2) {
            timer = 60
            while (timer > 0) {
                delay(1000L)
                timer--
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
                    BasicTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            isPhoneNumberValid = validatePhoneNumber(phoneNumber)
                        },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
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
                            if (isPhoneNumberValid && !isSendingSMS) {
                                isSendingSMS = true
                                scope.launch {
                                    try {
                                        Log.d("SignUp", "Sending SMS to $phoneNumber")
                                        firebaseSMSAuth.sendSMS(phoneNumber)
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
                        text = "Or sign up with your email",
                        fontSize = 12.sp,
                        color = Color.Red,
                        modifier = Modifier.clickable {
                            step = 3
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
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
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "If you didn't receive a code, Resend",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
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
                            .padding(16.dp)
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
                        onClick = {
                            scope.launch {
                                try {
                                    firebaseEmailAuth.confirm()
                                    step = 5 // Proceed to the next screen after verification

                                } catch (e: Exception) {
                                    errorMessage = "Failed to check verification status: ${e.message}"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Check Verification Status", color = Color.White)
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
                    Spacer(modifier = Modifier.height(16.dp))
                    BasicTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            isUsernameValid = validateUsername(username)
                        },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                    if (!isUsernameValid) {
                        Text(
                            "Invalid username",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Create a password",
                        fontSize = 30.sp,
                        color = Color(0xFFB71C1C),
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BasicTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            isPasswordValid = validatePassword(password.text)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                    if (!isPasswordValid) {
                        Text(
                            "Password must be at least 8 characters",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    BasicTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            doPasswordsMatch = password.text == confirmPassword.text
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(Color.LightGray, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(16.dp)
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
                                // Perform signup and navigation
                                isSignUpSuccessful = true
                                scope.launch {
                                    try {
                                        // call sync
                                        GenAuth.syncForSignUp(username, password.toString())
                                        navController.navigate("signin")
                                    } catch (e: Exception) {
                                        Log.d("Signup", e.toString())
                                        errorMessage = "Sign up failed. Please try again."
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Create an account", color = Color.White)
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
}

// Function to validate the phone number
fun validatePhoneNumber(phoneNumber: String): Boolean {
    // Example validation: Check if the phone number is not empty and contains exactly 10 digits
//    return phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    return true
}

// Function to validate the password
fun validatePassword(password: String): Boolean {
    // Example validation: Check if the password is at least 8 characters long
    return password.length >= 8
}

fun validateUsername(username: String): Boolean {
    return username.isNotEmpty() && username.length >= 3
}