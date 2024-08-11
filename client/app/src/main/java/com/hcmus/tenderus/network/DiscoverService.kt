package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.ui.viewmodels.ProfileResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GetProfile {
    @GET("api/recommendation")
    suspend fun getProfiles(@Header("Authorization") token: String, @Query("limit") limit: String): ProfileResponse
}
