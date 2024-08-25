package com.hcmus.tenderus.utils.firebase

import android.app.PendingIntent.getActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.model.UserRegistration
import com.hcmus.tenderus.network.ApiClient.LoginApi
import com.hcmus.tenderus.network.ApiClient.ProcessProfile
import com.hcmus.tenderus.network.ApiClient.SignOutApi
import com.hcmus.tenderus.network.ApiClient.SyncPasswordResetApi
import com.hcmus.tenderus.network.ApiClient.SyncSignUpApi
import com.hcmus.tenderus.network.AuthOKResponse
import com.hcmus.tenderus.network.LoginOKResponse
import kotlinx.coroutines.tasks.await

class GenAuth {
    companion object {
        suspend fun login(userLogin: UserLogin): LoginOKResponse {
            val res = LoginApi.login(userLogin)
            Firebase.auth.signInWithCustomToken(res.firebaseToken).await()
            TokenManager.saveToken(res)
            return res
        }
        suspend fun signOut() {
            if (TokenManager.getToken() != null && TokenManager.getRole() != "GUEST") {
                SignOutApi.signOut("Bearer " + TokenManager.getToken())
                Firebase.auth.signOut()
            }
            TokenManager.clearToken()
        }

        suspend fun syncForSignUp(username: String, password: String): AuthOKResponse {
            val mUser = Firebase.auth.currentUser!!
            val userRegistration = UserRegistration(username, password, mUser.getIdToken(true).await().token!!)
            return SyncSignUpApi.sync(userRegistration)
        }

        suspend fun syncForPasswordReset(password: String) {
            val mUser = Firebase.auth.currentUser!!
            val userRegistration = UserRegistration("", password, mUser.getIdToken(true).await().token!!)
            SyncPasswordResetApi.sync(userRegistration)
        }

    }
}