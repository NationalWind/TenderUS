package com.hcmus.tenderus.firebase

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.*
import com.hcmus.tenderus.model.User
import com.hcmus.tenderus.network.ApiClient.SyncSignUpWithEmailApi
import com.hcmus.tenderus.network.ApiClient.SyncSignUpWithSMSApi

import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await


class FirebaseEmailAuth(private val auth: FirebaseAuth, private val act: Activity) {
    private val TAG = "Firebase Auth Email"

    suspend fun signUpAndSendEmail(email: String, password: String) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
        } catch(e: FirebaseAuthUserCollisionException) {
            auth.signInWithEmailAndPassword(email, password).await()
        }
        // Sign in success, update UI with the signed-in user's information
        val user = auth.currentUser
        if (user!!.isEmailVerified) {
            throw Exception("Email has already been taken")
        }
        user.sendEmailVerification().await()
        Log.d(TAG, "Email sent.")
    }

    suspend fun confirmAndSync(user: User) {
        do {
            delay(2000)
            auth.currentUser!!.reload().await()
        } while (auth.currentUser!!.isEmailVerified == false)
        user.token = auth.currentUser!!.getIdToken(true).await().token!!
        SyncSignUpWithEmailApi.sync(user)
    }
}