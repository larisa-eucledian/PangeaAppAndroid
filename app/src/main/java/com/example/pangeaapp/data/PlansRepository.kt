package com.example.pangeaapp.data

import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.PackageRow

interface PlansRepository {
    suspend fun getCountries(): List<CountryRow>
    suspend fun getPackages(): List<PackageRow>

    suspend fun getPackagesByCountry(code: String): List<PackageRow>

    suspend fun getPackagesForCoverage(codes: List<String>): List<PackageRow>
}