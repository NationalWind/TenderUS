package com.hcmus.tenderus.data

import android.content.Context
import android.content.SharedPreferences
import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.AccountAction
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.ReportAction
import com.hcmus.tenderus.network.TenderUsApiService

object TokenManager {

    private const val PREF_NAME = "MyAppPreferences"
    private const val TOKEN_KEY = "jwt_token"
//    private const val USERNAME_KEY = "username"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }


}

interface TenderUsRepository {
    suspend fun getReportList(): List<Report>
    suspend fun getReportDetail(id: String): Report
    suspend fun postReportAction(id: String, reportAction: ReportAction)
    suspend fun getAccountList(): List<Account>
    suspend fun getAccountDetail(id: String): Account
    suspend fun postAccountAction(id: String, accountAction: AccountAction)
}

class NetworkTenderUsRepository(
    private val tenderUsApiService: TenderUsApiService
) : TenderUsRepository {
    override suspend fun getReportList(): List<Report> = tenderUsApiService.getReportList()
    override suspend fun getReportDetail(id: String): Report =
        tenderUsApiService.getReportDetail(id)

    override suspend fun postReportAction(id: String, reportAction: ReportAction) =
        tenderUsApiService.postReportAction(id, reportAction)

    override suspend fun getAccountList(): List<Account> = tenderUsApiService.getAccountList()
    override suspend fun getAccountDetail(id: String): Account =
        tenderUsApiService.getAccountDetail(id)

    override suspend fun postAccountAction(id: String, accountAction: AccountAction) =
        tenderUsApiService.postAccountAction(id, accountAction)
}