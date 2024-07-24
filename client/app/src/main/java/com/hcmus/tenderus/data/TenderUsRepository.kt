package com.hcmus.tenderus.data

import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.network.TenderUsApiService

interface TenderUsRepository {
    suspend fun getAccounts(): List<Account>
}

class NetworkTenderUsRepository(
    private val tenderUsApiService: TenderUsApiService
) : TenderUsRepository {
    override suspend fun getAccounts(): List<Account> = tenderUsApiService.getAccounts()
}