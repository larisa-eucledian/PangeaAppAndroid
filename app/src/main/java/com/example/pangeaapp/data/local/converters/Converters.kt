package com.example.pangeaapp.data.local.converters

import androidx.room.TypeConverter
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.data.local.entities.CountryEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromStringMap(value: Map<String, String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringMap(value: String?): Map<String, String>? {
        return value?.let {
            val type = object : TypeToken<Map<String, String>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromCurrencyMap(value: Map<String, CountryEntity.CurrencyInfo>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toCurrencyMap(value: String?): Map<String, CountryEntity.CurrencyInfo>? {
        return value?.let {
            val type = object : TypeToken<Map<String, CountryEntity.CurrencyInfo>>() {}.type
            gson.fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromGeography(value: Geography): String = value.name

    @TypeConverter
    fun toGeography(value: String): Geography = Geography.valueOf(value)
}