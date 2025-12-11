package com.example.pangeaapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.pangeaapp.data.local.converters.Converters
import com.example.pangeaapp.data.local.dao.CountryDao
import com.example.pangeaapp.data.local.dao.ESimDao
import com.example.pangeaapp.data.local.dao.PackageDao
import com.example.pangeaapp.data.local.entities.CountryEntity
import com.example.pangeaapp.data.local.entities.ESimEntity
import com.example.pangeaapp.data.local.entities.PackageEntity

@Database(
    entities = [CountryEntity::class, PackageEntity::class, ESimEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PangeaDatabase : RoomDatabase() {
    abstract fun countryDao(): CountryDao
    abstract fun packageDao(): PackageDao
    abstract fun esimDao(): ESimDao
}