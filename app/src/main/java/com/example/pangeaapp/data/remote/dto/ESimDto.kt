package com.example.pangeaapp.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for GET /esims
 */
data class ESimsResponseDto(
    @SerializedName("data") val data: List<ESimDto>,
    @SerializedName("meta") val meta: MetaDto?
) {
    data class MetaDto(
        @SerializedName("pagination") val pagination: PaginationDto?
    ) {
        data class PaginationDto(
            @SerializedName("total") val total: Int
        )
    }
}

/**
 * DTO for individual eSIM from API
 */
data class ESimDto(
    @SerializedName("id") val id: Int,
    @SerializedName("documentId") val documentId: String,
    @SerializedName("esim_id") val esimId: String,
    @SerializedName("iccid") val iccid: String?,
    @SerializedName("esim_status") val esimStatus: String,
    @SerializedName("activation_date") val activationDate: String?,
    @SerializedName("expiration_date") val expirationDate: String?,
    @SerializedName("package_name") val packageName: String,
    @SerializedName("package_id") val packageId: String,
    @SerializedName("number") val number: String?,
    @SerializedName("coverage") val coverage: List<String>,
    @SerializedName("user_email") val userEmail: String,
    @SerializedName("payment_intent_id") val paymentIntentId: String,
    @SerializedName("qr_code_url") val qrCodeUrl: String?,
    @SerializedName("lpa_code") val lpaCode: String?,
    @SerializedName("smdp_address") val smdpAddress: String?,
    @SerializedName("activation_code") val activationCode: String?,
    @SerializedName("ios_quick_install") val iosQuickInstall: String?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,
    @SerializedName("publishedAt") val publishedAt: String?,
    @SerializedName("locale") val locale: String?
)

/**
 * Request DTO for POST /esim/activate
 */
data class ActivateESimRequest(
    @SerializedName("esim_id") val esimId: String
)

/**
 * Response DTO for POST /esim/activate
 */
data class ActivateESimResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("esim") val esim: ESimDto
)
