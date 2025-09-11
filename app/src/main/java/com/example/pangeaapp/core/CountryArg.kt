package com.example.pangeaapp.core

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountryArg (
    val countryCode: String,
    val countryName: String,
    val coverageCodes: ArrayList<String> = arrayListOf()
) : Parcelable