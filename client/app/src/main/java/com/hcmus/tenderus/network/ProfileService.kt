package com.hcmus.tenderus.network


import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.model.Preference
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface ProfileService {

    @GET("api/profile/prof")
    suspend fun getCurrentUserProfile(@Header("Authorization") token: String): Profile

    @GET("api/profile/pref")
    suspend fun getCurrentUserPreferences(@Header("Authorization") token: String): Preference

    @POST("api/profile/uPref")
    suspend fun updateUserPreferences(@Header("Authorization") token: String, @Body preferences: Preference)

    @POST("api/profile/uProf")
    suspend fun updateUserProfile(@Header("Authorization") token: String, @Body profile: Profile)

    @POST("api/profile/cPref")
    suspend fun createUserPreferences(@Header("Authorization") token: String, @Body preferences: Preference)

    @POST("api/profile/cProf")
    suspend fun createUserProfile(@Header("Authorization") token: String, @Body profile: Profile)
}