package com.example.pangeaapp.di

import android.content.Context
import com.example.pangeaapp.core.security.TinkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideTinkManager(
        @ApplicationContext context: Context
    ): TinkManager {
        return TinkManager(context)
    }
}
