package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Report
import retrofit2.http.GET

interface TenderUsApiService {
    @GET("api/admin/report")
    suspend fun getReportList(): List<Report>
}