package com.hcmus.tenderus.network


import com.hcmus.tenderus.model.User
import retrofit2.http.Body
import retrofit2.http.POST

data class AuthOKResponse(private val message: String)

interface SyncSignUpWithSMS {
    @POST("api/auth/register")
    suspend fun sync(@Body user: User): AuthOKResponse
}

interface SyncSignUpWithEmail {
    @POST("api/auth/register")
    suspend fun sync(@Body user: User): AuthOKResponse
}