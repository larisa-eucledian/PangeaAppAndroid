package com.example.pangeaapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for GET /esim/usage/{esimId}
 */
data class ESimUsageResponseDto(
    @SerializedName("esim_id") val esimId: String,
    @SerializedName("iccid") val iccid: String,
    @SerializedName("package_name") val packageName: String,
    @SerializedName("usage") val usage: UsageDataDto
)

/**
 * Usage data from API
 */
data class UsageDataDto(
    @SerializedName("status") val status: String,
    @SerializedName("data") val data: UsageDetailsDto
)

/**
 * Detailed usage information
 */
data class UsageDetailsDto(
    @SerializedName("iccid") val iccid: String,
    @SerializedName("status") val status: String,
    @SerializedName("started_at") val startedAt: Long?,
    @SerializedName("expired_at") val expiredAt: Long?,
    @SerializedName("allowed_data") val allowedData: Long,
    @SerializedName("remaining_data") val remainingData: Long,
    @SerializedName("allowed_sms") val allowedSms: Int,
    @SerializedName("remaining_sms") val remainingSms: Int,
    @SerializedName("allowed_voice") val allowedVoice: Int,
    @SerializedName("remaining_voice") val remainingVoice: Int
)
