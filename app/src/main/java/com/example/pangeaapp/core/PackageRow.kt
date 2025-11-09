package com.example.pangeaapp.core

data class PackageRow(
    val id: Int,
    val documentId: String,
    val packageId: String,
    val packageName: String,
    val validityDays: Int,
    val pricePublic: Double,
    val dataAmount: String,
    val dataUnit: String,
    val callType: String? = null,
    val callAmount: String? = null,
    val callUnit: String? = null,
    val smsType: String? = null,
    val smsAmount: String? = null,
    val smsUnit: String? = null,
    val withSMS: Boolean? = null,
    val withCall: Boolean? = null,
    val withHotspot: Boolean? = null,
    val withDataRoaming: Boolean? = null,
    val withUsageCheck: Boolean? = null,
    val currency: String? = null,
    val geography: Geography,
    val coverage: List<String>?,
    val countryName: String
) {
    fun dataLabel(): String {
        val amt = dataAmount.trim()
        val looksUnlimited = amt.equals("unlimited", true) || amt == "9007199254740991"
        return if (looksUnlimited) "Unlimited" else "$amt $dataUnit"
    }

    fun kind(): String = when {
        dataLabel().equals("Unlimited", true) -> "Unlimited"
        (withCall == true) -> "Data & Calls"
        else -> "Only Data"
    }

    fun featuresList(): List<String> {
        val parts = mutableListOf<String>()
        parts += dataLabel()
        if (withCall == true)    parts += "Calls"
        if (withSMS == true)     parts += "SMS"
        if (withHotspot == true) parts += "Hotspot"
        return parts
    }
}
