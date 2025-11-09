package com.example.pangeaapp.data.remote.dto

import com.example.pangeaapp.core.Geography
import com.google.gson.annotations.SerializedName

data class PackageDto(
    val id: Int,
    @SerializedName("documentId") val documentId: String,
    @SerializedName("package_id") val packageId: String,
    @SerializedName("package") val `package`: String,
    @SerializedName("validity_days") val validityDays: Int,
    @SerializedName("price_public") val pricePublic: Double,
    @SerializedName("dataAmount") val dataAmount: String,
    @SerializedName("dataUnit") val dataUnit: String,
    @SerializedName("withSMS") val withSMS: Boolean = false,
    @SerializedName("withCall") val withCall: Boolean = false,
    @SerializedName("withHotspot") val withHotspot: Boolean = false,
    @SerializedName("withDataRoaming") val withDataRoaming: Boolean = false,
    @SerializedName("withUsageCheck") val withUsageCheck: Boolean = false,
    val currency: String?,
    val geography: Geography,
    val coverage: List<String>?,
    @SerializedName("country_name") val countryName: String
)