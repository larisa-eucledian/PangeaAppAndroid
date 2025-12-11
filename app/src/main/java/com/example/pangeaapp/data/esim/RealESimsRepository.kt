package com.example.pangeaapp.data.esim

import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.core.network.ConnectivityObserver
import com.example.pangeaapp.data.Resource
import com.example.pangeaapp.data.local.dao.ESimDao
import com.example.pangeaapp.data.mappers.toDomain
import com.example.pangeaapp.data.mappers.toEntity
import com.example.pangeaapp.data.remote.PangeaApiService
import com.example.pangeaapp.data.remote.dto.ActivateESimRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real implementation of ESimsRepository with network-first strategy
 */
@Singleton
class RealESimsRepository @Inject constructor(
    private val apiService: PangeaApiService,
    private val esimDao: ESimDao,
    private val connectivityObserver: ConnectivityObserver
) : ESimsRepository {

    override fun getESimsFlow(): Flow<Resource<List<ESimRow>>> = flow {
        android.util.Log.d("RealESimsRepo", "=== Starting getESimsFlow ===")

        // 1. Emit Loading with cached data (if available)
        val cachedEsims = try {
            esimDao.getAllESimsFlow().first().map { it.toDomain() }
        } catch (e: Exception) {
            android.util.Log.e("RealESimsRepo", "Error reading cache: ${e.message}", e)
            emptyList()
        }

        if (cachedEsims.isNotEmpty()) {
            android.util.Log.d("RealESimsRepo", "Found ${cachedEsims.size} cached eSIMs")
            emit(Resource.Loading(cachedEsims))
        } else {
            android.util.Log.d("RealESimsRepo", "No cached eSIMs found")
            emit(Resource.Loading(null))
        }

        // 2. NETWORK FIRST: Always try to fetch from network first
        if (connectivityObserver.isOnline()) {
            try {
                android.util.Log.d("RealESimsRepo", "Fetching from network...")
                val response = apiService.getESims()
                val freshEsims = response.data
                android.util.Log.d("RealESimsRepo", "API returned ${freshEsims.size} eSIMs")

                // Save to cache
                android.util.Log.d("RealESimsRepo", "Saving to cache...")
                esimDao.deleteAll()
                val entities = freshEsims.map { dto ->
                    android.util.Log.d("RealESimsRepo", "Converting DTO to Entity: ${dto.packageName}")
                    dto.toEntity()
                }
                esimDao.insertAll(entities)
                android.util.Log.d("RealESimsRepo", "Saved to cache successfully")

                // Emit fresh data
                val domainEsims = freshEsims.map { dto ->
                    android.util.Log.d("RealESimsRepo", "Converting DTO to Domain: ${dto.packageName}")
                    dto.toDomain()
                }
                android.util.Log.d("RealESimsRepo", "Emitting ${domainEsims.size} eSIMs as Success")
                emit(Resource.Success(domainEsims))
            } catch (e: Exception) {
                android.util.Log.e("RealESimsRepo", "Network error: ${e.message}", e)
                e.printStackTrace()
                // Network failed - use cache as fallback
                if (cachedEsims.isNotEmpty()) {
                    emit(Resource.Success(cachedEsims))
                } else {
                    emit(Resource.Error(e.message ?: "Unknown error"))
                }
            }
        } else {
            android.util.Log.d("RealESimsRepo", "Device is offline")
            // Offline - use cache
            if (cachedEsims.isNotEmpty()) {
                emit(Resource.Success(cachedEsims))
            } else {
                emit(Resource.Error("No internet connection"))
            }
        }
    }

    override suspend fun activateESim(esimId: String): Result<ESimRow> = try {
        val request = ActivateESimRequest(esimId = esimId)
        val response = apiService.activateESim(request)

        // Update cache
        val entity = response.esim.toEntity()
        esimDao.update(entity)

        Result.success(entity.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun invalidateCache() {
        esimDao.deleteAll()
    }

    override suspend fun refresh() {
        // Trigger manual refresh by invalidating cache
        // The Flow will automatically refetch when re-collected
        invalidateCache()
    }
}
