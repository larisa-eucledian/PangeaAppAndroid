package com.example.pangeaapp.di

import com.example.pangeaapp.data.auth.AuthRepository
import com.example.pangeaapp.data.auth.RealAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        realAuthRepository: RealAuthRepository
    ): AuthRepository
}
