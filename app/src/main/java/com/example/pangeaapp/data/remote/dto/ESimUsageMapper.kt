package com.example.pangeaapp.data.remote.dto

import com.example.pangeaapp.core.ESimUsage

/**
 * Map ESimUsageResponseDto to domain model ESimUsage
 */
fun ESimUsageResponseDto.toDomain(): ESimUsage {
    val usageData = this.usage.data
        ?: throw IllegalStateException("Usage data is not available for eSIM ${this.esimId}")

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
