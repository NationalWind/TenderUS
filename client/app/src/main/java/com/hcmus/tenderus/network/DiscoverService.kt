package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.ui.viewmodels.ProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


data class LikeRequest(val username: String, val likedUsername: String)
data class LikeResponse(val match: Boolean)

data class PassRequest(val username: String, val passedUsername: String)
data class PassResponse(val message: String)

data class PollingResponse(val message: String)

interface DiscoverService {
    @GET("api/recommendation")
    suspend fun getProfiles(@Header("Authorization") token: String, @Query("limit") limit: String): ProfileResponse

    @POST("api/swipe/like")
    suspend fun likeProfile(
        @Header("Authorization") token: String,
        @Body likeRequest: LikeRequest
    ): LikeResponse

    @POST("api/swipe/pass")
    suspend fun passProfile(
        @Header("Authorization") token: String,
        @Body passRequest: PassRequest
    ): PassResponse

    @GET("api/swipe/polling")
    suspend fun matchLongPoll(
        @Header("Authorization") token: String,
        @Query("username") username: String
    ): PollingResponse
}
