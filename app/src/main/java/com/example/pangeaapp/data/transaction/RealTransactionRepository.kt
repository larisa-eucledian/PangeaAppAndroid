package com.example.pangeaapp.data.transaction

import com.example.pangeaapp.data.remote.PangeaApiService
import com.example.pangeaapp.data.remote.dto.TransactionRequest
import com.example.pangeaapp.data.remote.dto.TransactionResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealTransactionRepository @Inject constructor(
    private val apiService: PangeaApiService
) : TransactionRepository {

    override suspend fun createStripeTransaction(
        amount: Double,
        packageId: String
    ): Result<TransactionResponse> = try {
        val request = TransactionRequest(
            amount = amount,
            currency = "MXN",  // Always MXN per requirements
            packageId = packageId,
            paymentMethod = "stripe"
        )

        val response = apiService.createTransaction(request)
        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
