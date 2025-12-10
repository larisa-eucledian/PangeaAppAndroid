package com.example.pangeaapp.core

import com.example.pangeaapp.BuildConfig
object Config {
    const val BASE_URL = "https://stage.pangeamovil.com/api/"
    val DEBUG = BuildConfig.DEBUG

    val STRIPE_PUBLISHABLE_KEY = BuildConfig.STRIPE_PUBLISHABLE_KEY
    val TENANT_API_KEY = BuildConfig.TENANT_API_KEY
}
