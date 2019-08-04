package com.ceaver.bao

import android.content.Context

class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}