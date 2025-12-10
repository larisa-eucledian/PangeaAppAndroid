package com.example.pangeaapp.data.remote

import com.example.pangeaapp.data.remote.dto.*
import retrofit2.http.*

interface PangeaApiService {

    // Auth
    @POST("auth/local")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/local/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): ForgotPasswordResponse

    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): AuthUser

    // Countries
    @GET("countries")
    suspend fun getCountries(
        @Query("geography") geography: String? = null
    ): List<CountryDto>

    // Packages
    @GET("tenant/packages")
    suspend fun getPackages(): Map<String, List<PackageDto>>

    @GET("tenant/packages")
    suspend fun getPackagesByCountry(
        @Query("country_code") countryCode: String
    ): List<PackageDto>

    // Transactions
    @POST("transactions")
    suspend fun createTransaction(
        @Body request: TransactionRequest
    ): TransactionResponse
}

data class LoginRequest(val identifier: String, val password: String)
data class RegisterRequest(val username: String, val email: String, val password: String)
data class ForgotPasswordRequest(val email: String)

data class AuthResponse(val jwt: String, val user: AuthUser)
data class AuthUser(
    val id: Int,
    val username: String,
    val email: String,
    val confirmed: Boolean?,
    val blocked: Boolean?
)
data class ForgotPasswordResponse(val ok: Boolean)