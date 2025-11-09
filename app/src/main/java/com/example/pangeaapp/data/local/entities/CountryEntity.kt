package com.example.pangeaapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.data.local.converters.Converters

@Entity(tableName = "countries")
@TypeConverters(Converters::class)
data class CountryEntity(
    @PrimaryKey val id: Int,
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
    val packageCount: Int?,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    data class CurrencyInfo(
        val name: String,
        val symbol: String?
    )
}