package com.example.pangeaapp.core

import com.example.pangeaapp.BuildConfig

/**
 * Config contiene constantes globales de la app
 */
object Config {
    const val BASE_URL = "https://stage.pangeamovil.com/api/"

    /**
     * Tenant API Key (fijo para esta app)
     * Se agrega automáticamente en AuthInterceptor
     */
    const val TENANT_API_KEY = "pga_pangeaapp_123"

    /**
     * Debug mode (basado en BuildConfig)
     */
    val DEBUG = BuildConfig.DEBUG

    /**
     * Timeouts para requests (en segundos)
     */
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    /**
     * Cache de imágenes (días)
     */
    const val IMAGE_CACHE_DAYS = 7

    /**
     * Refresh de datos (minutos)
     * Tiempo mínimo entre refreshes de countries/packages
     */
    const val DATA_REFRESH_INTERVAL_MINUTES = 60
}
