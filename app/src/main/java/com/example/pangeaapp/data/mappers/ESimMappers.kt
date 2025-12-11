package com.example.pangeaapp.data.mappers

import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.core.ESimStatus
import com.example.pangeaapp.data.local.entities.ESimEntity
import com.example.pangeaapp.data.remote.dto.ESimDto
import com.google.gson.Gson

/**
 * Convert DTO to Entity (for Room cache)
 */
fun ESimDto.toEntity(): ESimEntity {
    return ESimEntity(
        id = id,
        documentId = documentId,
        esimId = esimId,
        iccid = iccid,
        status = esimStatus,
        activationDate = activationDate,
        expirationDate = expirationDate,
        packageName = packageName,
        packageId = packageId,
        number = number,
        coverage = Gson().toJson(coverage),  // Convert list to JSON string
        userEmail = userEmail,
        paymentIntentId = paymentIntentId,
        qrCodeUrl = qrCodeUrl,
        lpaCode = lpaCode,
        smdpAddress = smdpAddress,
        activationCode = activationCode,
        iosQuickInstall = iosQuickInstall,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt
    )
}

/**
 * Convert Entity to Domain model
 */
fun ESimEntity.toDomain(): ESimRow {
    return ESimRow(
        id = id,
        documentId = documentId,
        esimId = esimId,
        iccid = iccid,
        status = ESimStatus.fromString(status),
        activationDate = activationDate,
        expirationDate = expirationDate,
        packageName = packageName,
        packageId = packageId,
        number = number,
        coverage = parseCoverageArray(coverage),
        userEmail = userEmail,
        paymentIntentId = paymentIntentId,
        qrCodeUrl = qrCodeUrl,
        lpaCode = lpaCode,
        smdpAddress = smdpAddress,
        activationCode = activationCode,
        iosQuickInstall = iosQuickInstall,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt
    )
}

/**
 * Convert DTO directly to Domain model (for API responses)
 */
fun ESimDto.toDomain(): ESimRow {
    return ESimRow(
        id = id,
        documentId = documentId,
        esimId = esimId,
        iccid = iccid,
        status = ESimStatus.fromString(esimStatus),
        activationDate = activationDate,
        expirationDate = expirationDate,
        packageName = packageName,
        packageId = packageId,
        number = number,
        coverage = coverage,
        userEmail = userEmail,
        paymentIntentId = paymentIntentId,
        qrCodeUrl = qrCodeUrl,
        lpaCode = lpaCode,
        smdpAddress = smdpAddress,
        activationCode = activationCode,
        iosQuickInstall = iosQuickInstall,
        createdAt = createdAt,
        updatedAt = updatedAt,
        publishedAt = publishedAt
    )
}

/**
 * Parse JSON string to List<String> for coverage
 */
private fun parseCoverageArray(json: String): List<String> {
    return try {
        Gson().fromJson(json, Array<String>::class.java).toList()
    } catch (e: Exception) {
        emptyList()
    }
}
