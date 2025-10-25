package com.example.pangeaapp.data.auth

/**
 * AuthRepository define el contrato para operaciones de autenticación.
 *
 * Sigue el mismo patrón que iOS (AuthRepository.swift)
 */
interface AuthRepository {
    /**
     * Login con email o username
     *
     * @param identifier Email o username del usuario
     * @param password Contraseña
     * @return AuthSession con JWT y user info
     * @throws AuthException si credenciales inválidas
     */
    suspend fun login(identifier: String, password: String): AuthSession

    /**
     * Registro de nuevo usuario
     *
     * @param username Username único
     * @param email Email válido
     * @param password Contraseña (mínimo 8 caracteres según API)
     * @return AuthSession con JWT y user info
     * @throws AuthException si datos inválidos o usuario ya existe
     */
    suspend fun register(username: String, email: String, password: String): AuthSession

    /**
     * Solicitar reset de contraseña
     *
     * @param email Email del usuario
     * @return true si el email fue enviado
     * @throws AuthException si el email no existe
     */
    suspend fun forgotPassword(email: String): Boolean

    /**
     * Obtener info del usuario actual (verifica token)
     *
     * @param jwt JWT token del usuario
     * @return AuthSession con user info actualizada
     * @throws AuthException si token inválido o expirado
     */
    suspend fun me(jwt: String): AuthSession
}

/**
 * AuthSession contiene los datos de una sesión autenticada
 * Coincide con la respuesta del API: { jwt, user }
 */
data class AuthSession(
    val jwt: String,
    val user: AuthUser
)

/**
 * AuthException para errores de autenticación
 */
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
