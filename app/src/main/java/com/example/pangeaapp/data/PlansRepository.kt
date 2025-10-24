package com.example.pangeaapp.data

import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.PackageRow
import kotlinx.coroutines.flow.Flow

interface PlansRepository {
    // Nuevos métodos con Flow
    fun getCountriesFlow(): Flow<Resource<List<CountryRow>>>
    fun getPackagesFlow(): Flow<Resource<List<PackageRow>>>
    fun getPackagesByCountryFlow(code: String): Flow<Resource<List<PackageRow>>>

    // Métodos antiguos (mantener por compatibilidad)
    suspend fun getCountries(): List<CountryRow>
    suspend fun getPackages(): List<PackageRow>
    suspend fun getPackagesByCountry(code: String): List<PackageRow>
    suspend fun getPackagesForCoverage(codes: List<String>): List<PackageRow>
}