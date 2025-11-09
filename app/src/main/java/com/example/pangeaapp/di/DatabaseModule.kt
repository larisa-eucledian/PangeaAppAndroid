package com.example.pangeaapp.di

import android.content.Context
import androidx.room.Room
import com.example.pangeaapp.data.local.PangeaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PangeaDatabase {
        return Room.databaseBuilder(
            context,
            PangeaDatabase::class.java,
            "pangea_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideCountryDao(database: PangeaDatabase) = database.countryDao()

    @Provides
    fun providePackageDao(database: PangeaDatabase) = database.packageDao()
}