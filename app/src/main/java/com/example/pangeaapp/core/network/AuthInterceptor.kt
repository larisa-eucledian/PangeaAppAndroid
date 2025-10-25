package com.example.pangeaapp.core.network

import com.example.pangeaapp.data.auth.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/**
 * AuthInterceptor agrega automáticamente:
 * 1. Authorization header con JWT (si el usuario está logueado)
 * 2. X-Tenant-API-Key header (siempre)
 *
 * Se ejecuta en TODAS las requests de Retrofit
 */
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_TENANT_KEY = "X-Tenant-API-Key"
        private const val TENANT_API_KEY = "pga_pangeaapp_123" // Valor fijo del API
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si la request ya tiene Authorization header, no sobreescribir
        // (útil para endpoints públicos o testing)
        val hasAuth = originalRequest.header(HEADER_AUTHORIZATION) != null
        val hasTenantKey = originalRequest.header(HEADER_TENANT_KEY) != null

        val requestBuilder = originalRequest.newBuilder()

        // Agregar JWT si el usuario está logueado y no tiene Auth header
        if (!hasAuth) {
            sessionManager.getBearerToken()?.let { token ->
                requestBuilder.addHeader(HEADER_AUTHORIZATION, token)
            }
        }

        // Agregar Tenant API Key si no lo tiene
        if (!hasTenantKey) {
            requestBuilder.addHeader(HEADER_TENANT_KEY, TENANT_API_KEY)
        }

        val request = requestBuilder.build()

        return chain.proceed(request)
    }
}
