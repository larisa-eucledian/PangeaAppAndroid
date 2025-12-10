package com.example.pangeaapp

import android.app.Application
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PangeaApp : Application() {

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase Analytics
        analytics = Firebase.analytics
        Log.d("PangeaApp", "Firebase Analytics initialized")

        // Enable analytics collection (can be toggled based on user consent)
        analytics.setAnalyticsCollectionEnabled(true)
    }
}