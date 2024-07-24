package com.hcmus.tenderus.fake

import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.network.TenderUsApiService

class FakeTenderUsApiService : TenderUsApiService {
    override suspend fun getAccounts(): List<Account> {
        return FakeDataSource.accountList
    }
}