package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Report
import retrofit2.http.GET
import retrofit2.http.Path

interface TenderUsApiService {
    @GET("api/admin/report")
    suspend fun getReportList(): List<Report>

    @GET("api/admin/report/{id}")
    suspend fun getReportDetail(@Path("id") id: String): Report
}