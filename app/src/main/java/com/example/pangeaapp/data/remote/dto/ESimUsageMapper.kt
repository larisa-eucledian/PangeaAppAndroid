package com.example.pangeaapp.data.remote.dto

import com.example.pangeaapp.core.ESimUsage

/**
 * Map ESimUsageResponseDto to domain model ESimUsage
 */
fun ESimUsageResponseDto.toDomain(): ESimUsage {
    return ESimUsage(
        esimId = this.esimId,
        iccid = this.iccid,
        packageName = this.packageName,
        status = this.usage.status,
        startedAt = this.usage.data.startedAt,
        expiredAt = this.usage.data.expiredAt,
        allowedData = this.usage.data.allowedData,
        remainingData = this.usage.data.remainingData,
        allowedSms = this.usage.data.allowedSms,
        remainingSms = this.usage.data.remainingSms,
        allowedVoice = this.usage.data.allowedVoice,
        remainingVoice = this.usage.data.remainingVoice
    )
}
