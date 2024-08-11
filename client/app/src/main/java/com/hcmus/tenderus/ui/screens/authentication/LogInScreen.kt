package com.hcmus.tenderus.ui.screens.authentication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.hcmus.tenderus.R
import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.ui.screens.BottomNavItem
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.utils.firebase.GenAuth
import com.hcmus.tenderus.utils.firebase.TenderUSPushNotificationService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    TenderUSTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController?.hide()
                    })
                }
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.tim),
                contentDescription = "logo",
                modifier = Modifier.size(170.dp)
            )
            Text(
                text = "Log In",
                fontSize = 55.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB71C1C)
            )
            Text(
                text = "We missed you!",
                fontSize = 14.sp,
                color = Color(0xFFB71C1C)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick =
                { /* Handle login logic here */
                    scope.launch {
                        try {
                            GenAuth.login(
                                UserLogin(
                                    username,
                                    password,
                                    FCMRegToken = TenderUSPushNotificationService.token!!
                                ), auth
                            )
                            navController.navigate(BottomNavItem.Discover.route)
                        } catch (e: Exception) {
                            Log.d("Login", e.toString())
                            // GUI error message here
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB71C1C),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "LOGIN")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Handle login as guest logic here */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "LOGIN AS GUEST")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Forgot password?",
                    modifier = Modifier.clickable {
                        // Handle forgot password click
                        navController.navigate("fgpass1")
                    },
                    color = Color.Red
                )
                Spacer(modifier = Modifier.width(32.dp)) // Use width instead of height for horizontal spacing
                Text(
                    text = "Don't have an account? Sign up",
                    modifier = Modifier.clickable {
                        navController.navigate("signup1")
                    },
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "By tapping \"Login\", You agree to our Terms & Conditions.",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Text(
                text = "Learn how we process your data in our Privacy & Policy.",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}