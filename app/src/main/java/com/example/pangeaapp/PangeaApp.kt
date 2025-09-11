package com.example.pangeaapp

import android.app.Application
import com.example.pangeaapp.di.AppDependencies

class PangeaApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppDependencies.init(this)
    }
}