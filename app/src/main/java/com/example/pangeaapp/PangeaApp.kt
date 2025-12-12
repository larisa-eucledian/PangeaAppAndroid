package com.example.pangeaapp

import android.app.Application
import com.example.pangeaapp.core.Config
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.stripe.android.PaymentConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PangeaApp : Application() {

    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate() {
        super.onCreate()

        analytics = Firebase.analytics
        analytics.setAnalyticsCollectionEnabled(true)

        PaymentConfiguration.init(
            applicationContext,
            Config.STRIPE_PUBLISHABLE_KEY
        )
    }
}