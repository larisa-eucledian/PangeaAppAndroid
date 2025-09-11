package com.example.pangeaapp.core

import java.io.Serializable

data class CountryRow (
    val id: Int,
    val documentId: String,
    val countryCode: String,
    val countryName: String,
    val region: String?,
    val imageUrl: String?,
    val languages: Map<String, String>?,
    val currencies: Map<String, CurrencyInfo>?,
    val callingCodes: List<String>?,
    val geography: Geography,
    val coveredCountries: List<String>?,
    val packageCount: Int?
) : Serializable {
    data class CurrencyInfo(
        val name: String,
        val symbol: String?
    )
}