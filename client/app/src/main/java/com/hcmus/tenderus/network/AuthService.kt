package com.hcmus.tenderus.network


import com.hcmus.tenderus.model.User
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthOKResponse(private val message: String)

interface SyncSignUp {
    @POST("api/auth/register")
    suspend fun sync(@Body user: User): AuthOKResponse
}
//
//interface SyncSignUpWithEmail {
//    @POST("api/auth/register")
//    suspend fun sync(@Body user: User): AuthOKResponse
//}

interface SyncPasswordReset {
    @POST("api/auth/resetPassword")
    suspend fun sync(@Body user: User): AuthOKResponse
}
//
//interface SyncResetPasswordWithEmail {
//    @POST("api/auth/register")
//    suspend fun sync(@Body user: User): AuthOKResponse
//}