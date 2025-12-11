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
        android.util.Log.d("RealPlansRepo", "getPackagesByCountryFlow called for: $code")
        return networkBoundResource(
            query = {
                android.util.Log.d("RealPlansRepo", "Query: Fetching from DB for country: $code")
                packageDao.getPackagesByCountry(code).map { entities ->
                    android.util.Log.d("RealPlansRepo", "DB returned ${entities.size} entities")
                    entities.map { it.toDomain() }
                }
            },
            fetch = {
                android.util.Log.d("RealPlansRepo", "Fetch: Calling API for country: $code")
                val result = apiService.getPackagesByCountry(code)
                android.util.Log.d("RealPlansRepo", "API returned ${result.size} packages")
                result
            },
            saveFetchResult = { packagesList ->
                android.util.Log.d("RealPlansRepo", "Saving ${packagesList.size} packages to DB")
                val entities = packagesList.map { it.toEntity() }
                packageDao.insertAll(entities)
                android.util.Log.d("RealPlansRepo", "Saved to DB successfully")
            },
            shouldFetch = {
                val online = connectivityObserver.isOnline()
                android.util.Log.d("RealPlansRepo", "Should fetch? Online: $online")
                online
            }
        )
    }
}