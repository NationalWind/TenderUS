package com.hcmus.tenderus.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.hcmus.tenderus.data.TokenManager
import com.hcmus.tenderus.model.Profile
import com.hcmus.tenderus.network.ApiClient.ProcessProfile
import kotlinx.coroutines.runBlocking

class ActivityStatusService: Service() {
    private val PREF_NAME = "MyAppPreferences"
    private val TOKEN_KEY = "jwt_token"

    private var token: String? = null

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        token = TokenManager.getToken()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        runBlocking {
            token?.let {

                ProcessProfile.updateUserProfile("Bearer $it", Profile(isActive = false))
            }
        }
        stopSelf()
    }
}