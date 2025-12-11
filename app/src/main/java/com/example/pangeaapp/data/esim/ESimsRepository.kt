package com.example.pangeaapp.data.esim

import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.data.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository for eSIMs management
 */
interface ESimsRepository {
    /**
     * Get all eSIMs for the authenticated user with network-first strategy
     * Returns a Flow that emits:
     * 1. Loading with cached data (if available)
     * 2. Success with fresh data from network
     * 3. Error with cached data as fallback (if network fails)
     */
    fun getESimsFlow(): Flow<Resource<List<ESimRow>>>

    /**
     * Activate an eSIM
     * @param esimId The eSIM ID to activate
     * @return Result with updated eSIM or error
     */
    suspend fun activateESim(esimId: String): Result<ESimRow>

    /**
     * Invalidate the cache (force refresh on next call)
     */
    suspend fun invalidateCache()

    /**
     * Manually trigger a refresh
     */
    suspend fun refresh()
}
