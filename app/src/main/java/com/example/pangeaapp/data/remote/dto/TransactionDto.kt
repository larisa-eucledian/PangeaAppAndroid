package com.example.pangeaapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TransactionRequest(
    val amount: Double,
    val currency: String = "MXN",
    @SerializedName("package_id")
    val packageId: String,
    @SerializedName("payment_method")
    val paymentMethod: String = "stripe"
)

data class TransactionResponse(
    @SerializedName("clientSecret")
    val clientSecret: String,
    @SerializedName("paymentIntentId")
    val paymentIntentId: String,
    @SerializedName("payment_method")
    val paymentMethod: String
)
