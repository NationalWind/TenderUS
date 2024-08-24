package com.hcmus.tenderus.ui.screens.authentication

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hcmus.tenderus.R
import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.network.LoginOKResponse
import com.hcmus.tenderus.ui.theme.TenderUSTheme
import com.hcmus.tenderus.utils.firebase.GenAuth
import com.hcmus.tenderus.utils.firebase.TenderUSPushNotificationService
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(navController: NavController, onLoggedIn: (res: LoginOKResponse) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    TenderUSTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
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
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    },
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                password = password,
                onPasswordChange = { password = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        try {
                            onLoggedIn(
                                GenAuth.login(
                                    UserLogin(
                                        username,
                                        password,
                                        FCMRegToken = TenderUSPushNotificationService.token!!
                                    )
                                )
                            )
                        } catch (e: HttpException) {
                            val errorBody = e.response()?.errorBody()?.string()
                            val errorJson = errorBody?.let { JSONObject(it) }
                            errorMessage = errorJson?.optString("message") ?: "An error occurred"
                            Log.d("Login", e.toString())
                        } catch (e: Exception) {
                            errorMessage = "An unexpected error occurred"
                            Log.d("Login", e.toString())
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

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Forgot password?",
                    modifier = Modifier.clickable {
                        navController.navigate("fgpass1")
                    },
                    color = Color(0xFFB71C1C)
                )
                Spacer(modifier = Modifier.width(40.dp))
                Text(
                    text = "Don't have an account? Sign up",
                    modifier = Modifier.clickable {
                        navController.navigate("signup1")
                    },
                    color = Color(0xFFB71C1C)
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    focusManager: FocusManager = LocalFocusManager.current
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
        ),
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = if (passwordVisible) "Hide password" else "Show password")
            }
        }
    )
}


