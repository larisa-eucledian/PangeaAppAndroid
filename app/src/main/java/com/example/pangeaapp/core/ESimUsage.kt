package com.example.pangeaapp.core

/**
 * Domain model for eSIM usage data
 */
data class ESimUsage(
    val esimId: String,
    val iccid: String,
    val packageName: String,
    val status: String,
    val startedAt: Long?,
    val expiredAt: Long?,
    val allowedData: Long,      // bytes
    val remainingData: Long,    // bytes
    val allowedSms: Int,
    val remainingSms: Int,
    val allowedVoice: Int,      // minutes
    val remainingVoice: Int     // minutes
) {
    /**
     * Get data usage percentage (0-100)
     */
    val dataUsagePercentage: Int
        get() = if (allowedData > 0) {
            ((allowedData - remainingData) * 100 / allowedData).toInt().coerceIn(0, 100)
        } else 0

    /**
     * Get SMS usage percentage (0-100)
     */
    val smsUsagePercentage: Int
        get() = if (allowedSms > 0) {
            ((allowedSms - remainingSms) * 100 / allowedSms).coerceIn(0, 100)
        } else 0

    /**
     * Get voice usage percentage (0-100)
     */
    val voiceUsagePercentage: Int
        get() = if (allowedVoice > 0) {
            ((allowedVoice - remainingVoice) * 100 / allowedVoice).coerceIn(0, 100)
        } else 0

    /**
     * Get data consumed in bytes
     */
    val dataConsumed: Long
        get() = (allowedData - remainingData).coerceAtLeast(0)

    /**
     * Get SMS consumed
     */
    val smsConsumed: Int
        get() = (allowedSms - remainingSms).coerceAtLeast(0)

    /**
     * Get voice consumed in minutes
     */
    val voiceConsumed: Int
        get() = (allowedVoice - remainingVoice).coerceAtLeast(0)
}
