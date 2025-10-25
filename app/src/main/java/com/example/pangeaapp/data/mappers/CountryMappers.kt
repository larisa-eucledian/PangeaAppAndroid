package com.example.pangeaapp.data.mappers

import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.data.local.entities.CountryEntity
import com.example.pangeaapp.data.remote.dto.CountryDto

// DTO -> Entity
fun CountryDto.toEntity() = CountryEntity(
    id = id.also { println("🟦 Mapping country: $countryName, geography from DTO: $geography") },
    documentId = documentId,
    countryCode = countryCode,
    countryName = countryName,
    region = region,
    imageUrl = imageUrl,
    languages = languages,
    currencies = currencies?.mapValues {
        CountryEntity.CurrencyInfo(it.value.name, it.value.symbol)
    },
    callingCodes = callingCodes,
    geography = geography,
    coveredCountries = coveredCountries,
    packageCount = packageCount
)

// Entity -> Domain
fun CountryEntity.toDomain() = CountryRow(
    id = id,
    documentId = documentId,
    countryCode = countryCode,
    countryName = countryName,
    region = region,
    imageUrl = imageUrl,
    languages = languages,
    currencies = currencies?.mapValues {
        CountryRow.CurrencyInfo(it.value.name, it.value.symbol)
    },
    callingCodes = callingCodes,
    geography = geography,
    coveredCountries = coveredCountries,
    packageCount = packageCount
)