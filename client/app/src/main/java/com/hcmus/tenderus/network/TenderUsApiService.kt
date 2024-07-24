package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Account
import retrofit2.http.POST

interface TenderUsApiService {
    @POST("api/auth/getAccounts")
    suspend fun getAccounts(): List<Account>
}