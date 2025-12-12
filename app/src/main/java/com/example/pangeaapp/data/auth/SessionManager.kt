package com.example.pangeaapp.data.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.pangeaapp.core.security.TinkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val tinkManager: TinkManager
) {
    companion object {
        private const val PREFS_NAME = "pangea_session_prefs"
        private const val LEGACY_PREFS_NAME = "pangea_secure_prefs"
        private const val KEY_JWT = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_MIGRATED = "tink_migrated"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val legacyPrefs: SharedPreferences? by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                LEGACY_PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            null
        }
    }

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: Flow<Boolean> = _isLoggedIn.asStateFlow()

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: Flow<UserInfo?> = _currentUser.asStateFlow()

    init {
        migrateFromLegacyIfNeeded()
        _isLoggedIn.value = hasValidSession()
        _currentUser.value = getCurrentUserInfo()
    }

    private fun migrateFromLegacyIfNeeded() {
        if (prefs.getBoolean(KEY_MIGRATED, false)) {
            return
        }

        legacyPrefs?.let { legacy ->
            val jwt = legacy.getString(KEY_JWT, null)
            val userId = legacy.getInt(KEY_USER_ID, -1)
            val username = legacy.getString(KEY_USERNAME, null)
            val email = legacy.getString(KEY_EMAIL, null)

            if (jwt != null && userId != -1 && username != null && email != null) {
                saveSession(
                    jwt = jwt,
                    user = AuthUser(
                        id = userId,
                        username = username,
                        email = email
                    )
                )

                legacy.edit().clear().apply()
            }
        }

        prefs.edit().putBoolean(KEY_MIGRATED, true).apply()
    }

    fun saveSession(jwt: String, user: AuthUser) {
        prefs.edit().apply {
            putString(KEY_JWT, tinkManager.encryptToBase64(jwt))
            putString(KEY_USER_ID, tinkManager.encryptToBase64(user.id.toString()))
            putString(KEY_USERNAME, tinkManager.encryptToBase64(user.username))
            putString(KEY_EMAIL, tinkManager.encryptToBase64(user.email))
            apply()
        }

        _isLoggedIn.value = true
        _currentUser.value = UserInfo(
            id = user.id,
            username = user.username,
            email = user.email
        )
    }

    fun getJwt(): String? {
        return try {
            prefs.getString(KEY_JWT, null)?.let { encrypted ->
                tinkManager.decryptFromBase64(encrypted)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getBearerToken(): String? {
        return getJwt()?.let { "Bearer $it" }
    }

    fun hasValidSession(): Boolean {
        return getJwt() != null
    }

    fun getCurrentUserInfo(): UserInfo? {
        return try {
            val userIdStr = prefs.getString(KEY_USER_ID, null)?.let {
                tinkManager.decryptFromBase64(it)
            } ?: return null

            val userId = userIdStr.toIntOrNull() ?: return null

            val username = prefs.getString(KEY_USERNAME, null)?.let {
                tinkManager.decryptFromBase64(it)
            } ?: ""

            val email = prefs.getString(KEY_EMAIL, null)?.let {
                tinkManager.decryptFromBase64(it)
            } ?: ""

            UserInfo(
                id = userId,
                username = username,
                email = email
            )
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _isLoggedIn.value = false
        _currentUser.value = null
    }

    fun updateJwt(newJwt: String) {
        prefs.edit().putString(KEY_JWT, tinkManager.encryptToBase64(newJwt)).apply()
    }
}

data class UserInfo(
    val id: Int,
    val username: String,
    val email: String
)

data class AuthUser(
    val id: Int,
    val username: String,
    val email: String,
    val confirmed: Boolean? = null,
    val blocked: Boolean? = null
)
