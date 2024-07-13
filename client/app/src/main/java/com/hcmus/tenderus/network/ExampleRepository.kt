// ExampleRepository.kt

package com.hcmus.tenderus.network

class ExampleRepository {
    private val apiService: ApiService = ApiClient.create(ApiService::class.java)

    suspend fun getExample(id: String): ExampleResponse {
        return apiService.getExample(id)
    }
}
