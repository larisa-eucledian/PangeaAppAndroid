package com.example.pangeaapp.data

import android.util.Log
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

    companion object {
        private const val TAG = "RealPlansRepository"
    }

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

    override fun getPackagesByCountryFlow(countryName: String, countryCode: String): Flow<Resource<List<PackageRow>>> {
        return networkBoundResource(
            query = {
                packageDao.getPackagesByCountryName(countryName).map { entities ->
                    entities.map { it.toDomain() }
                }
            },
            fetch = {
                apiService.getPackagesByCountry(countryName)
            },
            saveFetchResult = { packagesList ->
                val entities = packagesList.map { it.toEntity() }
                packageDao.deleteAll()
                packageDao.insertAll(entities)
            },
            shouldFetch = {
                connectivityObserver.isOnline()
            }
        )
    }

    override suspend fun getPackageById(packageId: String): Result<PackageRow> {
        return try {
            Log.d(TAG, "Fetching package by ID: $packageId")
            val packages = apiService.getPackageById(packageId)
            Log.d(TAG, "Package response received: ${packages.size} packages")
            if (packages.isNotEmpty()) {
                val packageRow = packages.first().toDomain()
                Log.d(TAG, "Package mapped to domain: $packageRow")
                Result.success(packageRow)
            } else {
                Log.w(TAG, "No package found for ID: $packageId")
                Result.failure(Exception("Package not found"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching package by ID: $packageId", e)
            Result.failure(e)
        }
    }
}
