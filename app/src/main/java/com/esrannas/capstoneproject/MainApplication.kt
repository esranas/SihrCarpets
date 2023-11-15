package com.esrannas.capstoneproject

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class  MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        shared = this
    }

    companion object {
        lateinit var shared : MainApplication

        val context : Context
            get() = shared.applicationContext

    }
}