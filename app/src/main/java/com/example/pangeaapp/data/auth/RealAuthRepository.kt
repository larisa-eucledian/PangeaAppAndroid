package com.example.pangeaapp.data.auth

import com.example.pangeaapp.data.remote.PangeaApiService
import com.example.pangeaapp.data.remote.ForgotPasswordRequest
import com.example.pangeaapp.data.remote.LoginRequest
import com.example.pangeaapp.data.remote.RegisterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RealAuthRepository @Inject constructor(
    private val apiService: PangeaApiService
) : AuthRepository {

    override suspend fun login(identifier: String, password: String): AuthSession {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(
                    identifier = identifier,
                    password = password
                )
                val response = apiService.login(request)
                AuthSession(
                    jwt = response.jwt,
                    user = AuthUser(
                        id = response.user.id,
                        username = response.user.username,
                        email = response.user.email,
                        confirmed = response.user.confirmed,
                        blocked = response.user.blocked
                    )
                )
            } catch (e: HttpException) {
                throw handleHttpException(e)
            } catch (e: IOException) {
                throw AuthException.NetworkError()
            } catch (e: Exception) {
                throw AuthException.UnknownError(e.message ?: "Unknown error")
            }
        }
    }
    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): AuthSession {
        return withContext(Dispatchers.IO) {
            try {
                validateRegistration(username, email, password)
                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )
                val response = apiService.register(request)
                AuthSession(
                    jwt = response.jwt,
                    user = AuthUser(
                        id = response.user.id,
                        username = response.user.username,
                        email = response.user.email,
                        confirmed = response.user.confirmed,
                        blocked = response.user.blocked
                    )
                )
            } catch (e: AuthException.ValidationError) {
                throw e // Re-throw validation errors
            } catch (e: HttpException) {
                throw handleHttpException(e)
            } catch (e: IOException) {
                throw AuthException.NetworkError()
            } catch (e: Exception) {
                throw AuthException.UnknownError(e.message ?: "Unknown error")
            }
        }
    }
    override suspend fun forgotPassword(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!isValidEmail(email)) {
                    throw AuthException.ValidationError("email", "Invalid email format")
                }
                val request = ForgotPasswordRequest(email = email)
                val response = apiService.forgotPassword(request)
                response.ok
            } catch (e: HttpException) {
                if (e.code() == 400) {
                    throw AuthException.EmailNotFound()
                }
                throw handleHttpException(e)
            } catch (e: IOException) {
                throw AuthException.NetworkError()
            } catch (e: Exception) {
                throw AuthException.UnknownError(e.message ?: "Unknown error")
            }
        }
    }

    override suspend fun me(jwt: String): AuthSession {
        return withContext(Dispatchers.IO) {
            try {
                val authHeader = "Bearer $jwt"
                val user = apiService.getCurrentUser(authHeader)
                AuthSession(
                    jwt = jwt,
                    user = AuthUser(
                        id = user.id,
                        username = user.username,
                        email = user.email,
                        confirmed = user.confirmed,
                        blocked = user.blocked
                    )
                )
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    throw AuthException.InvalidToken()
                }
                throw handleHttpException(e)
            } catch (e: IOException) {
                throw AuthException.NetworkError()
            } catch (e: Exception) {
                throw AuthException.UnknownError(e.message ?: "Unknown error")
            }
        }
    }
    private fun handleHttpException(e: HttpException): AuthException {
        return when (e.code()) {
            400 -> {
                val errorBody = e.response()?.errorBody()?.string()
                if (errorBody?.contains("already exists", ignoreCase = true) == true) {
                    AuthException.UserAlreadyExists()
                } else if (errorBody?.contains("invalid", ignoreCase = true) == true) {
                    AuthException.InvalidCredentials()
                } else {
                    AuthException.ValidationError("request", "Invalid data")
                }
            }
            401 -> AuthException.InvalidCredentials()
            404 -> AuthException.EmailNotFound()
            500 -> AuthException.UnknownError("Server error")
            else -> AuthException.UnknownError("HTTP ${e.code()}")
        }
    }

    private fun validateRegistration(username: String, email: String, password: String) {
        if (username.length < 3 || username.length > 50) {
            throw AuthException.ValidationError(
                "username",
                "Must be between 3 and 50 characters"
            )
        }

        if (!username.matches(Regex("^[a-zA-Z0-9_-]+$"))) {
            throw AuthException.ValidationError(
                "username",
                "Only letters, numbers, underscore and dash allowed"
            )
        }

        if (!isValidEmail(email)) {
            throw AuthException.ValidationError("email", "Invalid email format")
        }

        if (password.length < 8) {
            throw AuthException.ValidationError(
                "password",
                "Must be at least 8 characters"
            )
        }
    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
