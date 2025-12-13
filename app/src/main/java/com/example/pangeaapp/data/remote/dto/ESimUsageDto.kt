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
    @SerializedName("data") val data: UsageDetailsDto?
)

/**
 * Detailed usage information
 */
data class UsageDetailsDto(
    @SerializedName("iccid") val iccid: String,
    @SerializedName("status") val status: String,
    @SerializedName("startedAt") val startedAt: Long?,
    @SerializedName("expiredAt") val expiredAt: Long?,
    @SerializedName("allowedData") val allowedData: Long,
    @SerializedName("remainingData") val remainingData: Long,
    @SerializedName("allowedSms") val allowedSms: Int,
    @SerializedName("remainingSms") val remainingSms: Int,
    @SerializedName("allowedVoice") val allowedVoice: Int,
    @SerializedName("remainingVoice") val remainingVoice: Int
)
