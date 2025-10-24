package com.example.pangeaapp.data.local.dao

import androidx.room.*
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.data.local.entities.CountryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryDao {
    @Query("SELECT * FROM countries ORDER BY countryName ASC")
    fun getAllCountriesFlow(): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE geography = :geography ORDER BY countryName ASC")
    fun getCountriesByGeography(geography: Geography): Flow<List<CountryEntity>>

    @Query("SELECT * FROM countries WHERE id = :id")
    suspend fun getCountryById(id: Int): CountryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(countries: List<CountryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: CountryEntity)

    @Query("DELETE FROM countries")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM countries")
    suspend fun getCountriesCount(): Int
}