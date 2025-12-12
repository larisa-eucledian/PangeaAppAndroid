package com.example.pangeaapp.data.local.dao

import androidx.room.*
import androidx.room.Dao
import com.example.pangeaapp.data.local.entities.PackageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackageDao {
    @Query("SELECT * FROM packages")
    fun getAllPackagesFlow(): Flow<List<PackageEntity>>

    @Query("""
    SELECT * FROM packages
    WHERE countryName = :countryName COLLATE NOCASE
""")
    fun getPackagesByCountryName(countryName: String): Flow<List<PackageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(packages: List<PackageEntity>)

    @Query("DELETE FROM packages")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM packages")
    suspend fun getPackagesCount(): Int
}