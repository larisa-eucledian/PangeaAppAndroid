package com.example.pangeaapp.core.network

import com.example.pangeaapp.data.auth.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_TENANT_KEY = "X-Tenant-API-Key"
        private const val TENANT_API_KEY = "pga_pangeaapp_123"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val hasAuth = originalRequest.header(HEADER_AUTHORIZATION) != null
        val hasTenantKey = originalRequest.header(HEADER_TENANT_KEY) != null
        val requestBuilder = originalRequest.newBuilder()

        if (!hasAuth) {
            sessionManager.getBearerToken()?.let { token ->
                requestBuilder.addHeader(HEADER_AUTHORIZATION, token)
            }
        }

        if (!hasTenantKey) {
            requestBuilder.addHeader(HEADER_TENANT_KEY, TENANT_API_KEY)
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
