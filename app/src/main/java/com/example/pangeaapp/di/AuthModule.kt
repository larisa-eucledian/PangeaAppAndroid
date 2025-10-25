package com.example.pangeaapp.di

import com.example.pangeaapp.data.auth.AuthRepository
import com.example.pangeaapp.data.auth.RealAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AuthModule provee las dependencias de autenticación para Hilt
 *
 * SessionManager no necesita binding porque usa @Inject constructor
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    /**
     * Provee la implementación de AuthRepository
     *
     * @Binds es más eficiente que @Provides para interfaces
     */
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        realAuthRepository: RealAuthRepository
    ): AuthRepository
}
