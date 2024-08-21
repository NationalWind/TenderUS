package com.hcmus.tenderus.network


import com.hcmus.tenderus.model.UserLogin
import com.hcmus.tenderus.model.UserRegistration
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

data class AuthOKResponse(val message: String)
data class LoginOKResponse(val token: String, val firebaseToken: String, val firstTime: Boolean, val role: String)

interface SyncSignUp {
    @POST("api/auth/register")
    suspend fun sync(@Body userRegistration: UserRegistration): AuthOKResponse
}
//
//interface SyncSignUpWithEmail {
//    @POST("api/auth/register")
//    suspend fun sync(@Body user: User): AuthOKResponse
//}

interface SyncPasswordReset {
    @POST("api/auth/resetPassword")
    suspend fun sync(@Body userRegistration: UserRegistration): AuthOKResponse
}
//
//interface SyncResetPasswordWithEmail {
//    @POST("api/auth/register")
//    suspend fun sync(@Body user: User): AuthOKResponse
//}

interface Login {
    @POST("api/auth/login")
    suspend fun login(@Body userLogin: UserLogin): LoginOKResponse
}

interface SignOut {
    @POST("api/auth/signOut")
    suspend fun signOut(@Header("Authorization") token: String): LoginOKResponse
}

