package com.example.pangeaapp.core.security

import android.content.Context
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.aead.AeadKeyTemplates
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import java.nio.charset.StandardCharsets

class TinkManager(private val context: Context) {

    private val aead: Aead by lazy {
        AeadConfig.register()

        val keysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
            .withKeyTemplate(AeadKeyTemplates.AES256_GCM)
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle

        keysetHandle.getPrimitive(Aead::class)
    }

    fun encrypt(plaintext: String): ByteArray {
        return aead.encrypt(
            plaintext.toByteArray(StandardCharsets.UTF_8),
            ASSOCIATED_DATA
        )
    }

    fun decrypt(ciphertext: ByteArray): String {
        val decrypted = aead.decrypt(ciphertext, ASSOCIATED_DATA)
        return String(decrypted, StandardCharsets.UTF_8)
    }

    fun encryptToBase64(plaintext: String): String {
        val encrypted = encrypt(plaintext)
        return android.util.Base64.encodeToString(
            encrypted,
            android.util.Base64.NO_WRAP
        )
    }

    fun decryptFromBase64(base64Ciphertext: String): String {
        val ciphertext = android.util.Base64.decode(
            base64Ciphertext,
            android.util.Base64.NO_WRAP
        )
        return decrypt(ciphertext)
    }

    companion object {
        private const val KEYSET_NAME = "pangea_keyset"
        private const val PREF_FILE_NAME = "pangea_tink_prefs"
        private const val MASTER_KEY_URI = "android-keystore://pangea_master_key"
        private val ASSOCIATED_DATA = "PangeaApp".toByteArray(StandardCharsets.UTF_8)
    }
}
