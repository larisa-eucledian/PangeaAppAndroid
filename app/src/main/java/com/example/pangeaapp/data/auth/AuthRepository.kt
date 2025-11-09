package com.example.pangeaapp.data.auth

interface AuthRepository {
    suspend fun login(identifier: String, password: String): AuthSession
    suspend fun register(username: String, email: String, password: String): AuthSession
    suspend fun forgotPassword(email: String): Boolean
    suspend fun me(jwt: String): AuthSession
}
data class AuthSession(
    val jwt: String,
    val user: AuthUser
)
sealed class AuthException(message: String) : Exception(message) {
    class InvalidCredentials : AuthException("Invalid email/username or password")
    class UserAlreadyExists : AuthException("User already exists")
    class EmailNotFound : AuthException("Email not found")
    class NetworkError : AuthException("Network error. Please try again")
    class InvalidToken : AuthException("Session expired. Please login again")
    class ValidationError(field: String, reason: String) :
        AuthException("$field: $reason")
    class UnknownError(message: String) : AuthException(message)
}
