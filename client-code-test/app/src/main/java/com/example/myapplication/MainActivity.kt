package com.example.myapplication
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*


import io.ktor.client.request.*
import io.ktor.client.statement.*

import io.ktor.http.*
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.*

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.util.concurrent.TimeUnit

@Serializable
data class User(val username: String, val password: String, val email: String, val phone: String, val token: String)

class InputViewModel : ViewModel() {
    private val _submittedText = MutableStateFlow("")
    val submittedText: StateFlow<String> = _submittedText

    fun submitText(text: String) {
        _submittedText.value = text
    }
}
class MainActivity : ComponentActivity() {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            connectTimeoutMillis = 15000
        }
    }
    private var phoneNumber = "+84phonenumber"
    private var TAG = "MainACT"
    private lateinit var auth: FirebaseAuth
    var code: String = ""
    private var storedVerificationId: String? = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    @Preview
    @Composable
    fun SimpleFilledTextFieldSample() {




        var text by remember { mutableStateOf("Hello") }

        Column {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Label") }
            )
            Button(onClick = {
                code = text
                val credential = PhoneAuthProvider.getCredential(storedVerificationId!!, code)
                Log.d(TAG, "Here3")
                signInWithPhoneAuthCredential(credential)




            }) {
                Text("Filled")
            }
        }
    }




    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val mUser = task.result?.user
                    Log.d(TAG, "Here4")

                    // Calls to launch should happen inside a LaunchedEffect and not composition
                    mUser!!.getIdToken(true)
                        .addOnCompleteListener { task1 ->
                            if (task1.isSuccessful) {
                                val idToken = task1.result.token
                                Log.d("idToken", idToken!!)
                                CoroutineScope(Dispatchers.Default).launch {
                                    val response: HttpResponse = client.post("http://192.168.1.251:8000/api/auth/register") {
                                        contentType(ContentType.Application.Json)
                                        setBody(User("nginx", "Jetty", "", phoneNumber, idToken!!))
                                    }
                                    Log.d("Res", response.bodyAsText())

                                }
                            } else {
                                // Handle error -> task.getException();
                                Log.w(TAG, "FAIL", task.exception)
                            }
                        }
                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                    // Update UI
                }
            }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                } else if (e is FirebaseAuthMissingActivityForRecaptchaException) {
                    // reCAPTCHA verification attempted with null Activity
                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        auth = Firebase.auth
        val phoneNumber = phoneNumber
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        Log.d(TAG, "Here1")
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d(TAG, "Here2")
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (
                        Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
                    ) {
                        SimpleFilledTextFieldSample()
                    }

                }
            }

        }


//        auth.createUserWithEmailAndPassword("ng.nguynv@gmail.com", "hihihahaha")
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("MainActivity", "createUserWithEmail:success")
//                    val user = auth.currentUser
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("ohshit", "createUserWithEmail:failure", task.exception)
//                }
//            }


    }
}

