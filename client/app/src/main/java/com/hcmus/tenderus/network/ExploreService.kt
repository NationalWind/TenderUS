package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.ui.viewmodels.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

data class ExploreOKResponse(val message: String)
data class JoinStatus(val joined: Boolean)

interface ExploreService {
    @GET("api/recommendation")
    suspend fun getProfiles(@Header("Authorization") token: String, @Query("limit") limit: String, @Query("group") group: String): ProfileResponse

    @GET("api/recommendation/join")
    suspend fun getJoinStatus(@Header("Authorization") token: String, @Query("group") group: String): JoinStatus

    @POST("api/recommendation/join")
    suspend fun join(@Header("Authorization") token: String, @Query("group") group: String): ExploreOKResponse


}
