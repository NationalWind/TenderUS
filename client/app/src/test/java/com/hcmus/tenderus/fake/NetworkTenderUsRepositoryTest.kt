package com.hcmus.tenderus.fake

import com.hcmus.tenderus.data.NetworkTenderUsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkTenderUsRepositoryTest {
    @Test
    fun networkTenderUsRepository_getAccounts() = runTest {
        val repository = NetworkTenderUsRepository(
            tenderUsApiService = FakeTenderUsApiService()
        )
        assertEquals(FakeDataSource.reportList, repository.getReportList())
    }
}