package com.hcmus.tenderus.firebase

import android.app.Activity
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.google.firebase.*
import com.google.firebase.auth.*
import com.hcmus.tenderus.model.User
import com.hcmus.tenderus.network.ApiClient.SyncSignUpApi
import com.hcmus.tenderus.network.ApiClient.SyncPasswordResetApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

class FirebaseSMSAuth(private val auth: FirebaseAuth, private val act: Activity) {
    private val TAG = "Firebase Auth SMS"
    private var storedVerificationId: String = ""
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            Log.d(TAG, "onVerificationCompleted:$credential")
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

    fun sendSMS(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(act) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        Log.d(TAG, "send")
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun confirmAndSync(user: User, otp: String, syncFor: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, otp)

        val result = auth.signInWithCredential(credential).await()
        // Sign in success, update UI with the signed-in user's information
        Log.d(TAG, "confirmSMS:success")

        val mUser = result.user
        val tokenResult = mUser!!.getIdToken(true).await()
        user.token = tokenResult.token!!
        if (syncFor == "SIGN_UP") {
            SyncSignUpApi.sync(user)
        } else if (syncFor == "RESET_PASSWORD") {
            SyncPasswordResetApi.sync(user)
        } else {
            throw Exception("Invalid syncFor")
        }

        Log.d(TAG, "Sync:success")
    }
}