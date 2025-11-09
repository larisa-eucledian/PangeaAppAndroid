package com.example.pangeaapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.data.local.converters.Converters

@Entity(tableName = "packages")
@TypeConverters(Converters::class)
data class PackageEntity(
    @PrimaryKey val id: Int,
    val documentId: String,
    val packageId: String,
    val packageName: String,
    val validityDays: Int,
    val pricePublic: Double,
    val dataAmount: String,
    val dataUnit: String,
    val withSMS: Boolean,
    val withCall: Boolean,
    val withHotspot: Boolean,
    val withDataRoaming: Boolean,
    val withUsageCheck: Boolean,
    val currency: String?,
    val geography: Geography,
    val coverage: List<String>?,
    val countryName: String,
    val lastUpdated: Long = System.currentTimeMillis()
)