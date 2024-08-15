package com.hcmus.tenderus.utils.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.*
import com.hcmus.tenderus.model.UserRegistration
import com.hcmus.tenderus.network.ApiClient.SyncSignUpApi
import com.hcmus.tenderus.network.ApiClient.SyncPasswordResetApi

import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await


class FirebaseEmailAuth(private val auth: FirebaseAuth, private val act: Activity) {
    private val TAG = "Firebase Auth Email"

    suspend fun sendEmail(email: String) {
        try {
            auth.createUserWithEmailAndPassword(email, email).await()
        } catch(e: FirebaseAuthUserCollisionException) {
            auth.signInWithEmailAndPassword(email, email).await()
        }
        // Sign in success, update UI with the signed-in user's information
        val user = auth.currentUser
        if (user!!.isEmailVerified) {
            throw Exception("Email has already been taken")
        }
        user.sendEmailVerification().await()
        Log.d(TAG, "Email sent.")

    }

    suspend fun confirmAndSync(userRegistration: UserRegistration, syncFor: String) {
        do {
            delay(1500)
            auth.currentUser!!.reload().await()
        } while (auth.currentUser!!.isEmailVerified == false)
        userRegistration.token = auth.currentUser!!.getIdToken(true).await().token!!
        if (syncFor == "SIGN_UP") {
            SyncSignUpApi.sync(userRegistration)
        } else if (syncFor == "RESET_PASSWORD") {
            SyncPasswordResetApi.sync(userRegistration)
        } else {
            throw Exception("Invalid syncFor")
        }
    }
    // New method to check if the email is verified
    suspend fun isEmailVerifiedSign(): Boolean {
        auth.currentUser?.reload()?.await()  // Reload the user's data from Firebase
        return auth.currentUser?.isEmailVerified ?: false
    }
}