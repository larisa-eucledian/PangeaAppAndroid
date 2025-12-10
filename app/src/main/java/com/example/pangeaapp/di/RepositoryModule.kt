package com.example.pangeaapp.di

import com.example.pangeaapp.data.PlansRepository
import com.example.pangeaapp.data.RealPlansRepository
import com.example.pangeaapp.data.transaction.RealTransactionRepository
import com.example.pangeaapp.data.transaction.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPlansRepository(
        realPlansRepository: RealPlansRepository
    ): PlansRepository

    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        realTransactionRepository: RealTransactionRepository
    ): TransactionRepository
}