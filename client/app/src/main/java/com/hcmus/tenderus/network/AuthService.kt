package com.hcmus.tenderus.network


import com.hcmus.tenderus.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface SyncSignUpWithSMS {
    @POST("api/auth/register")
    suspend fun sync(@Body user: User): String
}