package com.example.pangeaapp.data.remote.dto

import com.example.pangeaapp.core.ESimUsage

/**
 * Map ESimUsageResponseDto to domain model ESimUsage
 */
fun ESimUsageResponseDto.toDomain(): ESimUsage {

    val usageDetails = usage.details
        ?: throw IllegalStateException("Usage data is not available for eSIM ${this.esimId}")
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
