package com.example.pangeaapp.data

import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.PackageRow
import kotlinx.coroutines.flow.Flow

interface PlansRepository {
    fun getCountriesFlow(): Flow<Resource<List<CountryRow>>>
    fun getPackagesFlow(): Flow<Resource<List<PackageRow>>>
    fun getPackagesByCountryFlow(code: String): Flow<Resource<List<PackageRow>>>

}