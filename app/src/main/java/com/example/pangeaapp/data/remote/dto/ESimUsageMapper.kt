package com.example.pangeaapp.data.remote.dto

import android.util.Log
import com.example.pangeaapp.core.ESimUsage

/**
 * Map ESimUsageResponseDto to domain model ESimUsage
 */
fun ESimUsageResponseDto.toDomain(): ESimUsage {
    Log.d("ESimUsageMapper", "=== MAPPER START ===")
    Log.d("ESimUsageMapper", "esimId: $esimId")
    Log.d("ESimUsageMapper", "usage.responseStatus: ${usage.responseStatus}")
    Log.d("ESimUsageMapper", "usage.data: ${usage.data}")

    val usageData = this.usage.data
        ?: throw IllegalStateException("Usage data is not available for eSIM ${this.esimId}")

    Log.d("ESimUsageMapper", "usageData.allowedData: ${usageData.allowedData}")
    Log.d("ESimUsageMapper", "usageData.remainingData: ${usageData.remainingData}")

    return ESimUsage(
        esimId = this.esimId,
        iccid = this.iccid,
        packageName = this.packageName,
        status = usageData.status,
        startedAt = usageData.startedAt,
        expiredAt = usageData.expiredAt,
        allowedData = usageData.allowedData,
        remainingData = usageData.remainingData,
        allowedSms = usageData.allowedSms,
        remainingSms = usageData.remainingSms,
        allowedVoice = usageData.allowedVoice,
        remainingVoice = usageData.remainingVoice
    )
}
