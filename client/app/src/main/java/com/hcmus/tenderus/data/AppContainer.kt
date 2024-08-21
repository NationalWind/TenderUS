package com.hcmus.tenderus.data

import com.hcmus.tenderus.network.TenderUsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val tenderUsRepository: TenderUsRepository
}

const val baseUrl = "http://172.29.192.25:8000/"

class DefaultAppContainer : AppContainer {
    // You may get an error here
    // 1. Make sure your client and server are on the same network
    // 2. Run ipconfig
    // 3. Copy IP4 address (some thing like 192.168.xxx.xxx)
    // 4. Replace the address below
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