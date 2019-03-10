package com.intmainreturn00.grexample

import android.app.Application
import com.intmainreturn00.grapi.grapi

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        grapi.init(this, BuildConfig.goodreadsKey, BuildConfig.goodreadsSecret, BuildConfig.goodreadsCallback)
    }
}