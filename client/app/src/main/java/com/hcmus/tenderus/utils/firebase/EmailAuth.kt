package com.hcmus.tenderus.utils.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.*
import com.hcmus.tenderus.model.UserRegistration
import com.hcmus.tenderus.network.ApiClient.SyncSignUpApi
import com.hcmus.tenderus.network.ApiClient.SyncPasswordResetApi

import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await


class FirebaseEmailAuth(private val act: Activity) {
    private val TAG = "Firebase Auth Email"

    suspend fun sendEmail(email: String) {
        try {
            Firebase.auth.createUserWithEmailAndPassword(email, email).await()
        } catch(e: FirebaseAuthUserCollisionException) {
            Firebase.auth.signInWithEmailAndPassword(email, email).await()
        }
        // Sign in success, update UI with the signed-in user's information
        val user = Firebase.auth.currentUser
        if (user!!.isEmailVerified) {
            throw Exception("Email has already been taken")
        }
        user.sendEmailVerification().await()
        Log.d(TAG, "Email sent.")

    }

    suspend fun confirm() {
        do {
            delay(1500)
            Firebase.auth.currentUser!!.reload().await()
        } while (!Firebase.auth.currentUser!!.isEmailVerified)
    }


}