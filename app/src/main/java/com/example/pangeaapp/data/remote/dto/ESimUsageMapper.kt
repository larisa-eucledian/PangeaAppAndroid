package com.example.pangeaapp.data.remote.dto

import android.util.Log
import com.example.pangeaapp.core.ESimUsage

/**
 * Map ESimUsageResponseDto to domain model ESimUsage
 */
fun ESimUsageResponseDto.toDomain(): ESimUsage {
    Log.d("ESimUsageMapper", "=== MAPPER START ===")
    Log.d("ESimUsageMapper", "esimId: $esimId")
    Log.d("ESimUsageMapper", "usage.apiStatus: ${usage.apiStatus}")
    Log.d("ESimUsageMapper", "usage.details: ${usage.details}")

    val usageDetails = usage.details
        ?: throw IllegalStateException("Usage data is not available for eSIM ${this.esimId}")

    Log.d("ESimUsageMapper", "usageDetails.esimStatus: ${usageDetails.esimStatus}")
    Log.d("ESimUsageMapper", "usageDetails.allowedData: ${usageDetails.allowedData}")
    Log.d("ESimUsageMapper", "usageDetails.remainingData: ${usageDetails.remainingData}")

    return ESimUsage(
        esimId = this.esimId,
        iccid = this.iccid,
        packageName = this.packageName,
        status = usageDetails.esimStatus,
        startedAt = usageDetails.startedAt,
        expiredAt = usageDetails.expiredAt,
        allowedData = usageDetails.allowedData,
        remainingData = usageDetails.remainingData,
        allowedSms = usageDetails.allowedSms,
        remainingSms = usageDetails.remainingSms,
        allowedVoice = usageDetails.allowedVoice,
        remainingVoice = usageDetails.remainingVoice
    )
}
