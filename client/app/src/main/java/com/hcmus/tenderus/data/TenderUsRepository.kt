package com.hcmus.tenderus.data

import android.content.Context
import android.content.SharedPreferences
import com.hcmus.tenderus.model.Account
import com.hcmus.tenderus.model.AccountAction
import com.hcmus.tenderus.model.Report
import com.hcmus.tenderus.model.ReportAction
import com.hcmus.tenderus.model.ReportData
import com.hcmus.tenderus.network.LoginOKResponse
import com.hcmus.tenderus.network.TenderUsApiService

object TokenManager {

    private const val PREF_NAME = "TENDER_US"
    private const val TOKEN_KEY = "TOKEN"
    private const val ROLE_KEY = "ROLE"
    private const val FIRST_TIME_KEY = "FIRST_TIME"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(res: LoginOKResponse) {
        sharedPreferences.edit().putString(TOKEN_KEY, res.token).apply()
        sharedPreferences.edit().putString(ROLE_KEY, res.role).apply()
        sharedPreferences.edit().putBoolean(FIRST_TIME_KEY, res.firstTime).apply()
    }

    fun saveFirstTime(firstTime: Boolean) {
        sharedPreferences.edit().putBoolean(FIRST_TIME_KEY, firstTime).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getRole(): String? {
        return sharedPreferences.getString(ROLE_KEY, "USER")
    }

    fun getFirstTime(): Boolean {
        return sharedPreferences.getBoolean(FIRST_TIME_KEY, false)
    }


    fun clearToken() {
        sharedPreferences.edit().remove(TOKEN_KEY).apply()
        sharedPreferences.edit().remove(ROLE_KEY).apply()
        sharedPreferences.edit().remove(FIRST_TIME_KEY).apply()
    }


}

interface TenderUsRepository {
    suspend fun getReportList(): List<Report>
    suspend fun getReportDetail(id: String): Report
    suspend fun postReport(report: ReportData)
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

    override suspend fun postReport(report: ReportData) = tenderUsApiService.postReport(report)
    override suspend fun postReportAction(id: String, reportAction: ReportAction) =
        tenderUsApiService.postReportAction(id, reportAction)

    override suspend fun getAccountList(): List<Account> = tenderUsApiService.getAccountList()
    override suspend fun getAccountDetail(id: String): Account =
        tenderUsApiService.getAccountDetail(id)

    override suspend fun postAccountAction(id: String, accountAction: AccountAction) =
        tenderUsApiService.postAccountAction(id, accountAction)
}