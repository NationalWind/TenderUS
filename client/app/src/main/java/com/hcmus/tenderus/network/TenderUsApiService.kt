package com.hcmus.tenderus.network

import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.ReportAction
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TenderUsApiService {
    @GET("api/admin/report")
    suspend fun getReportList(): List<Report>

    @GET("api/admin/report/{id}")
    suspend fun getReportDetail(@Path("id") id: String): Report

    @POST("api/admin/report/{id}")
    suspend fun postReportAction(@Path("id") id: String, @Body reportAction: ReportAction)

    @GET("api/admin/account")
    suspend fun getAccountList(): List<Account>
}