package com.example.pangeaapp.data.local.dao

import androidx.room.*
import com.example.pangeaapp.data.local.entities.ESimEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ESimDao {
    /**
     * Get all eSIMs sorted by status (ready, installed, expired) then by creation date
     */
    @Query("SELECT * FROM esims ORDER BY status ASC, createdAt DESC")
    fun getAllESimsFlow(): Flow<List<ESimEntity>>

    /**
     * Get a specific eSIM by its esimId
     */
    @Query("SELECT * FROM esims WHERE esimId = :esimId")
    suspend fun getESimById(esimId: String): ESimEntity?

    /**
     * Insert or replace eSIMs
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(esims: List<ESimEntity>)

    /**
     * Update an existing eSIM
     */
    @Update
    suspend fun update(esim: ESimEntity)

    /**
     * Delete all eSIMs (for cache invalidation)
     */
    @Query("DELETE FROM esims")
    suspend fun deleteAll()

    /**
     * Get count of eSIMs
     */
    @Query("SELECT COUNT(*) FROM esims")
    suspend fun getESimsCount(): Int
}
