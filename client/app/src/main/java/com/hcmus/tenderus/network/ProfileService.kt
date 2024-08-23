package com.hcmus.tenderus.network


import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.model.Preference
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface ProfileService {

    @GET("api/profile/pref")
    suspend fun getCurrentUserPreferences(@Header("Authorization") token: String): Preference

    @GET("api/profile/prof")
    suspend fun getCurrentUserProfile(@Header("Authorization") token: String): Profile

    @POST("api/profile/Pref")
    suspend fun upsertUserPreferences(@Header("Authorization") token: String, @Body preferences: Preference)

    @POST("api/profile/Prof")
    suspend fun upsertUserProfile(@Header("Authorization") token: String, @Body profile: Profile)

}