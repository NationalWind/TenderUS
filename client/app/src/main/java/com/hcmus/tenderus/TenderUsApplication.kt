package com.hcmus.tenderus

import android.app.Application
import com.hcmus.tenderus.data.AppContainer
import com.hcmus.tenderus.data.DefaultAppContainer

class TenderUsApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}