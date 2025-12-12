package com.example.pangeaapp.core

/**
 * Domain model for eSIM
 */
data class ESimRow(
    val id: Int,
    val documentId: String,
    val esimId: String,  // UUID
    val iccid: String?,
    val status: ESimStatus,
    val activationDate: String?,
    val expirationDate: String?,
    val packageName: String,
    val packageId: String,
    val number: String?,
    val coverage: List<String>,  // Array of country codes
    val userEmail: String,
    val paymentIntentId: String,

    // QR Code / Activation info
    val qrCodeUrl: String?,
    val lpaCode: String?,
    val smdpAddress: String?,
    val activationCode: String?,
    val iosQuickInstall: String?,

    // Timestamps
    val createdAt: String?,
    val updatedAt: String?,
    val publishedAt: String?
) {
    /**
     * Check if eSIM is active/installed
     */
    val isActive: Boolean
        get() = status == ESimStatus.INSTALLED

    /**
     * Check if eSIM is ready for activation
     */
    val isReady: Boolean
        get() = status == ESimStatus.READY_FOR_ACTIVATION

    /**
     * Check if eSIM is expired
     */
    val isExpired: Boolean
        get() = status == ESimStatus.EXPIRED

    /**
     * Sort order for status (for UI display)
     * READY -> INSTALLED -> EXPIRED -> UNKNOWN
     */
    val statusSortOrder: Int
        get() = when (status) {
            ESimStatus.READY_FOR_ACTIVATION -> 0
            ESimStatus.INSTALLED -> 1
            ESimStatus.EXPIRED -> 2
            ESimStatus.UNKNOWN -> 3
        }
}

/**
 * eSIM status enum
 */
enum class ESimStatus(val value: String) {
    READY_FOR_ACTIVATION("READY FOR ACTIVATION"),
    INSTALLED("INSTALLED"),
    EXPIRED("EXPIRED"),
    UNKNOWN("UNKNOWN");

    companion object {
        /**
         * Parse status from API string
         */
        fun fromString(value: String?): ESimStatus {
            if (value == null) return UNKNOWN

            return when (value.uppercase().trim()) {
                "READY FOR ACTIVATION", "READY_FOR_ACTIVATION", "READY" -> READY_FOR_ACTIVATION
                "INSTALLED", "ACTIVE" -> INSTALLED
                "EXPIRED" -> EXPIRED
                else -> UNKNOWN
            }
        }
    }
}
