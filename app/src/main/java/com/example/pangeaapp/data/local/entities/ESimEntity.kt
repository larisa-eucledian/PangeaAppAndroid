package com.example.pangeaapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "esims")
data class ESimEntity(
    @PrimaryKey val id: Int,
    val documentId: String,
    val esimId: String,
    val iccid: String?,
    val status: String,
    val activationDate: String?,
    val expirationDate: String?,
    val packageName: String,
    val packageId: String,
    val number: String?,
    val coverage: String,  // JSON string of array
    val userEmail: String,
    val paymentIntentId: String,
    val qrCodeUrl: String?,
    val lpaCode: String?,
    val smdpAddress: String?,
    val activationCode: String?,
    val iosQuickInstall: String?,
    val createdAt: String?,
    val updatedAt: String?,
    val publishedAt: String?,
    val lastUpdated: Long = System.currentTimeMillis()
)
