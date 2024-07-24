package com.hcmus.tenderus.data

import com.hcmus.tenderus.network.TenderUsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val tenderUsRepository: TenderUsRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://localhost:8000"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()

    private val retrofitService: TenderUsApiService by lazy {
        retrofit.create(TenderUsApiService::class.java)
    }

    override val tenderUsRepository: TenderUsRepository by lazy {
        NetworkTenderUsRepository(retrofitService)
    }
}