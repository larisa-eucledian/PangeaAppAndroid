package com.example.pangeaapp.data

import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.PackageRow
import com.example.pangeaapp.core.network.ConnectivityObserver
import com.example.pangeaapp.data.local.dao.CountryDao
import com.example.pangeaapp.data.local.dao.PackageDao
import com.example.pangeaapp.data.mappers.toDomain
import com.example.pangeaapp.data.mappers.toEntity
import com.example.pangeaapp.data.remote.PangeaApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealPlansRepository @Inject constructor(
    private val apiService: PangeaApiService,
    private val countryDao: CountryDao,
    private val packageDao: PackageDao,
    private val connectivityObserver: ConnectivityObserver
) : PlansRepository {

    override fun getCountriesFlow(): Flow<Resource<List<CountryRow>>> {
        return networkBoundResource(
            query = {
                countryDao.getAllCountriesFlow().map { entities ->
                    entities.map { it.toDomain() }
                }
            },
            fetch = {
                apiService.getCountries()
            },
            saveFetchResult = { dtos ->
                val entities = dtos
                    .map { it.toEntity() }
                countryDao.deleteAll()
                countryDao.insertAll(entities)
            },
            shouldFetch = {
                connectivityObserver.isOnline()
            }
        )
    }

    override fun getPackagesFlow(): Flow<Resource<List<PackageRow>>> {
        return networkBoundResource(
            query = {
                packageDao.getAllPackagesFlow().map { entities ->
                    entities.map { it.toDomain() }
                }
            },
            fetch = {
                apiService.getPackages()
            },
            saveFetchResult = { packagesMap ->
                val allPackages = packagesMap.values.flatten()
                val entities = allPackages.map { it.toEntity() }
                packageDao.deleteAll()
                packageDao.insertAll(entities)
            },
            shouldFetch = {
                connectivityObserver.isOnline()
            }
        )
    }

    override fun getPackagesByCountryFlow(code: String): Flow<Resource<List<PackageRow>>> {
        return networkBoundResource(
            query = {
                packageDao.getPackagesByCountry(code).map { entities ->
                    entities.map { it.toDomain() }
                }
            },
            fetch = {
                apiService.getPackagesByCountry(code)
            },
            saveFetchResult = { packagesMap ->
                val packages = packagesMap.values.flatten()
                val entities = packages.map { it.toEntity() }
                packageDao.insertAll(entities)
            },
            shouldFetch = {
                connectivityObserver.isOnline()
            }
        )
    }

    // Mantener métodos suspend para compatibilidad temporal
    override suspend fun getCountries(): List<CountryRow> {
        TODO("Deprecated - usar getCountriesFlow()")
    }

    override suspend fun getPackages(): List<PackageRow> {
        TODO("Deprecated - usar getPackagesFlow()")
    }

    override suspend fun getPackagesByCountry(code: String): List<PackageRow> {
        TODO("Deprecated - usar getPackagesByCountryFlow()")
    }

    override suspend fun getPackagesForCoverage(codes: List<String>): List<PackageRow> {
        TODO("Refactorizar usando Flow")
    }
}