// ApiService.kt

package com.hcmus.tenderus.network

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("endpoint/{id}")
    suspend fun getExample(@Path("id") id: String): ExampleResponse
}
