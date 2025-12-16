package com.example.pangeaapp.data.esim

import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.core.ESimUsage
import com.example.pangeaapp.core.network.ConnectivityObserver
import com.example.pangeaapp.data.Resource
import com.example.pangeaapp.data.local.dao.ESimDao
import com.example.pangeaapp.data.mappers.toDomain
import com.example.pangeaapp.data.mappers.toEntity
import com.example.pangeaapp.data.remote.PangeaApiService
import com.example.pangeaapp.data.remote.dto.ActivateESimRequest
import com.example.pangeaapp.data.remote.dto.toDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
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
        val cachedEsims = try {
            esimDao.getAllESimsFlow().first().map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }

        if (cachedEsims.isNotEmpty()) {
            emit(Resource.Loading(cachedEsims))
        } else {
            emit(Resource.Loading(null))
        }

        if (connectivityObserver.isOnline()) {
            try {
                val response = apiService.getESims()
                val freshEsims = response.data

                esimDao.deleteAll()
                esimDao.insertAll(freshEsims.map { it.toEntity() })

                emit(Resource.Success(freshEsims.map { it.toDomain() }))
            } catch (e: Exception) {
                if (cachedEsims.isNotEmpty()) {
                    emit(Resource.Success(cachedEsims))
                } else {
                    emit(Resource.Error(e.message ?: "Unknown error"))
                }
            }
        } else {
            if (cachedEsims.isNotEmpty()) {
                emit(Resource.Success(cachedEsims))
            } else {
                emit(Resource.Error("No internet connection"))
            }
        }
    }

    override suspend fun getESimById(esimId: String): ESimRow? = withContext(Dispatchers.IO) {
        return@withContext try {
            esimDao.getESimById(esimId)?.toDomain()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun activateESim(esimId: String): Result<ESimRow> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = ActivateESimRequest(esimId = esimId)
            val response = apiService.activateESim(request)

            val entity = response.esim.toEntity()
            esimDao.update(entity)

            Result.success(entity.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsage(esimId: String): Result<ESimUsage> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = apiService.getESimUsage(esimId)
            Result.success(response.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun invalidateCache() = withContext(Dispatchers.IO) {
        esimDao.deleteAll()
    }

    override suspend fun refresh() {
        // Trigger manual refresh by invalidating cache
        // The Flow will automatically refetch when re-collected
        invalidateCache()
    }
}
