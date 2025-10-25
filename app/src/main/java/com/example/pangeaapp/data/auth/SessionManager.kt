package com.example.pangeaapp.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SessionManager maneja la sesión del usuario de forma segura.
 *
 * Usa EncryptedSharedPreferences (con Tink internamente) para datos sensibles como JWT.
 * Expone Flows para observar cambios de sesión reactivamente.
 */
@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "pangea_secure_prefs"
        private const val KEY_JWT = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }

    // EncryptedSharedPreferences para JWT (dato sensible)
    private val encryptedPrefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // StateFlow para observar estado de login
    private val _isLoggedIn = MutableStateFlow(hasValidSession())
    val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()

    // StateFlow para observar user info
    private val _currentUser = MutableStateFlow(getCurrentUserInfo())
    val currentUser: Flow<UserInfo?> = _currentUser.asStateFlow()

    /**
     * Guarda la sesión después de login/register exitoso
     */
    fun saveSession(jwt: String, user: AuthUser) {
        encryptedPrefs.edit().apply {
            putString(KEY_JWT, jwt)
            putInt(KEY_USER_ID, user.id)
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            apply()
        }

        _isLoggedIn.value = true
        _currentUser.value = UserInfo(
            id = user.id,
            username = user.username,
            email = user.email
        )
    }

    /**
     * Obtiene el JWT actual (para agregarlo a requests)
     */
    fun getJwt(): String? {
        return encryptedPrefs.getString(KEY_JWT, null)
    }

    /**
     * Obtiene el JWT con formato Bearer para headers
     */
    fun getBearerToken(): String? {
        return getJwt()?.let { "Bearer $it" }
    }

    /**
     * Verifica si hay una sesión válida
     */
    fun hasValidSession(): Boolean {
        return getJwt() != null
    }

    /**
     * Obtiene info del usuario actual
     */
    fun getCurrentUserInfo(): UserInfo? {
        val userId = encryptedPrefs.getInt(KEY_USER_ID, -1)
        if (userId == -1) return null

        return UserInfo(
            id = userId,
            username = encryptedPrefs.getString(KEY_USERNAME, "") ?: "",
            email = encryptedPrefs.getString(KEY_EMAIL, "") ?: ""
        )
    }

    /**
     * Cierra sesión y limpia datos
     */
    fun logout() {
        encryptedPrefs.edit().clear().apply()
        _isLoggedIn.value = false
        _currentUser.value = null
    }

    /**
     * Actualiza el JWT (útil para refresh tokens en el futuro)
     */
    fun updateJwt(newJwt: String) {
        encryptedPrefs.edit().putString(KEY_JWT, newJwt).apply()
    }
}

/**
 * Data class para info del usuario (no sensible)
 */
data class UserInfo(
    val id: Int,
    val username: String,
    val email: String
)

/**
 * Data class para AuthUser (debe coincidir con el del API)
 */
data class AuthUser(
    val id: Int,
    val username: String,
    val email: String,
    val confirmed: Boolean? = null,
    val blocked: Boolean? = null
)
