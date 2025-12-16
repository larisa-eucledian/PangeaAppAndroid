package com.example.pangeaapp.data.mappers

import com.example.pangeaapp.core.PackageRow
import com.example.pangeaapp.data.local.entities.PackageEntity
import com.example.pangeaapp.data.remote.dto.PackageDto

fun PackageDto.toDomain() = PackageRow(
    id = id,
    documentId = documentId,
    packageId = packageId,
    packageName = `package`,
    validityDays = validityDays,
    pricePublic = pricePublic,
    dataAmount = dataAmount,
    dataUnit = dataUnit,
    withSMS = withSMS,
    withCall = withCall,
    withHotspot = withHotspot,
    withDataRoaming = withDataRoaming,
    withUsageCheck = withUsageCheck,
    currency = currency,
    geography = geography,
    coverage = coverage,
    countryName = countryName
)

fun PackageDto.toEntity() = PackageEntity(
    id = id,
    documentId = documentId,
    packageId = packageId,
    packageName = `package`,
    validityDays = validityDays,
    pricePublic = pricePublic,
    dataAmount = dataAmount,
    dataUnit = dataUnit,
    withSMS = withSMS,
    withCall = withCall,
    withHotspot = withHotspot,
    withDataRoaming = withDataRoaming,
    withUsageCheck = withUsageCheck,
    currency = currency,
    geography = geography,
    coverage = coverage,
    countryName = countryName
)

fun PackageEntity.toDomain() = PackageRow(
    id = id,
    documentId = documentId,
    packageId = packageId,
    packageName = packageName,
    validityDays = validityDays,
    pricePublic = pricePublic,
    dataAmount = dataAmount,
    dataUnit = dataUnit,
    withSMS = withSMS,
    withCall = withCall,
    withHotspot = withHotspot,
    withDataRoaming = withDataRoaming,
    withUsageCheck = withUsageCheck,
    currency = currency,
    geography = geography,
    coverage = coverage,
    countryName = countryName
)