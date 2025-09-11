package com.example.pangeaapp.core

import java.io.Serializable

data class PackageRow (
    val id: Int,
    val documentId: String,
    val packageId: String,
    val `package`: String,
    val validityDays: Int,
    val pricePublic: Double,
    val dataAmount: String,
    val dataUnit: String,
    val withSMS: Boolean?,
    val withCall: Boolean?,
    val withHotspot: Boolean?,
    val withDataRoaming: Boolean?,
    val withUsageCheck: Boolean?,
    val currency: String?,
    val geography: Geography,
    val coverage: List<String>?,
    val countryName: String
) : Serializable {
    fun dataLabel(): String {
        val amt = dataAmount.trim()
        val looksUnlimited = amt.equals("unlimited",true) || amt == "9007199254740991"
        return if (looksUnlimited) "Unlimited" else "$amt $dataUnit"
    }

    fun kind(): String = when {
        dataLabel().equals("Unlimited", true) -> "Unlimited"
        (withCall == true) -> "Data & Calls"
        else -> "Only Data"
    }

    fun featuresLine(): String {
        val parts = mutableListOf<String>()
        parts += dataLabel()
        if (withCall == true) parts += "Calls"
        if (withSMS == true) parts += "SMS"
        if (withHotspot == true) parts += "Hotspot"
        return parts.joinToString("  • ")
    }
}