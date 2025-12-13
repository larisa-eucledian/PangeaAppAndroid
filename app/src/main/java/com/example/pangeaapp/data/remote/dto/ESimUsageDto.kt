package com.example.pangeaapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for GET /esim/usage/{esimId}
 *
 * Flattened structure to avoid Gson parsing issues with nested "status" fields
 */
data class ESimUsageResponseDto(
    @SerializedName("esim_id") val esimId: String,
    @SerializedName("iccid") val iccid: String,
    @SerializedName("package_name") val packageName: String,
    @SerializedName("usage") val usage: UsageWrapperDto
)

/**
 * Wrapper that contains status and nested data
 */
data class UsageWrapperDto(
    @SerializedName("status") val apiStatus: String,  // "success" or "error"
    @SerializedName("data") val details: UsageDetailsDto?
)

/**
 * Detailed usage information
 */
data class UsageDetailsDto(
    @SerializedName("iccid") val iccid: String,
    @SerializedName("status") val esimStatus: String,  // "NEW", "ACTIVATED", etc.
    @SerializedName("startedAt") val startedAt: Long?,
    @SerializedName("expiredAt") val expiredAt: Long?,
    @SerializedName("allowedData") val allowedData: Long,
    @SerializedName("remainingData") val remainingData: Long,
    @SerializedName("allowedSms") val allowedSms: Int,
    @SerializedName("remainingSms") val remainingSms: Int,
    @SerializedName("allowedVoice") val allowedVoice: Int,
    @SerializedName("remainingVoice") val remainingVoice: Int
)
