package com.example.pangeaapp.data.transaction

import com.example.pangeaapp.data.remote.dto.TransactionResponse

interface TransactionRepository {
    /**
     * Create a Stripe transaction for purchasing an eSIM package
     *
     * @param amount Price of the package
     * @param packageId ID of the package being purchased
     * @return Result with TransactionResponse containing clientSecret for Stripe PaymentSheet
     */
    suspend fun createStripeTransaction(
        amount: Double,
        packageId: String
    ): Result<TransactionResponse>
}
