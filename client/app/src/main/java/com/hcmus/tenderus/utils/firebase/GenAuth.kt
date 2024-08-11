package com.hcmus.tenderus.utils.firebase

import com.google.firebase.auth.FirebaseAuth
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.network.ApiClient.LoginApi
import com.hcmus.tenderus.network.ApiClient.SignOutApi
import kotlinx.coroutines.tasks.await

class GenAuth {
    companion object {
        suspend fun login(userLogin: UserLogin, auth: FirebaseAuth) {
            val res = LoginApi.login(userLogin)
            auth.signInWithCustomToken(res.firebaseToken).await()
            TokenManager.saveToken(res.token)
        }
        suspend fun signOut() {
            SignOutApi.signOut("Bearer " + TokenManager.getToken()!!)
            TokenManager.clearToken()
        }
    }
}