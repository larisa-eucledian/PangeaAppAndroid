package com.example.pangeaapp.data.remote.dto

import com.example.pangeaapp.core.Geography
import com.google.gson.annotations.SerializedName

data class CountryDto(
    val id: Int,
    @SerializedName("documentId") val documentId: String,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("country_name") val countryName: String,
    val region: String?,
    @SerializedName("image_url") val imageUrl: String?,
    val languages: Map<String, String>?,
    val currencies: Map<String, CurrencyInfoDto>?,
    @SerializedName("callingCodes") val callingCodes: List<String>?,
    val geography: Geography,
    @SerializedName("covered_countries") val coveredCountries: List<String>?,
    @SerializedName("packageCount") val packageCount: Int?
) {
    data class CurrencyInfoDto(
        val name: String,
        val symbol: String?
    )
}