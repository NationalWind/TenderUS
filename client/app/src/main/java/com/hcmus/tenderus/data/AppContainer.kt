package com.hcmus.tenderus.data

import com.hcmus.tenderus.network.ApiClient
import com.hcmus.tenderus.network.TenderUsApiService

interface AppContainer {
    val tenderUsRepository: TenderUsRepository
}

const val imageBaseUrl = "http://4.217.254.66:8080/"

class DefaultAppContainer : AppContainer {
    private val retrofit = ApiClient.retrofit
    private val retrofitService: TenderUsApiService by lazy {
        retrofit.create(TenderUsApiService::class.java)
    }

    override val tenderUsRepository: TenderUsRepository by lazy {
        NetworkTenderUsRepository(retrofitService)
    }
}