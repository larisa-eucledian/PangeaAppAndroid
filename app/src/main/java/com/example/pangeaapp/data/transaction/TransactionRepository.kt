package com.example.pangeaapp.data.transaction

import com.example.pangeaapp.data.remote.dto.TransactionResponse

interface TransactionRepository {
    suspend fun createStripeTransaction(
        amount: Double,
        packageId: String
    ): Result<TransactionResponse>
}
