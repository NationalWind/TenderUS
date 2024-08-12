package com.hcmus.tenderus.fake

import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.network.TenderUsApiService

class FakeTenderUsApiService : TenderUsApiService {
    override suspend fun getReportList(): List<Report> {
        return FakeDataSource.reportList
    }
}