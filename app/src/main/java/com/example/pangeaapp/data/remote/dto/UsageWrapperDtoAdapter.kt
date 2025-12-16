package com.example.pangeaapp.data.remote.dto

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * Custom TypeAdapter to correctly parse UsageWrapperDto
 *
 * Gson has a bug with nested "status" fields - it reads the inner status
 * instead of the outer one. This adapter manually parses the JSON to fix that.
 */
class UsageWrapperDtoAdapter : TypeAdapter<UsageWrapperDto>() {

    override fun write(out: JsonWriter, value: UsageWrapperDto?) {
        // We don't need to serialize, only deserialize
        throw UnsupportedOperationException("UsageWrapperDtoAdapter doesn't support write")
    }

    override fun read(`in`: JsonReader): UsageWrapperDto {
        var status: String? = null
        var details: UsageDetailsDto? = null

        `in`.beginObject()
        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "status" -> status = `in`.nextString()
                "data" -> details = readUsageDetails(`in`)
                else -> `in`.skipValue()
            }
        }
        `in`.endObject()

        return UsageWrapperDto(
            status = status ?: "unknown",
            details = details
        )
    }

    private fun readUsageDetails(`in`: JsonReader): UsageDetailsDto? {
        if (`in`.peek() == com.google.gson.stream.JsonToken.NULL) {
            `in`.nextNull()
            return null
        }

        var iccid: String? = null
        var status: String? = null
        var startedAt: Long? = null
        var expiredAt: Long? = null
        var allowedData: Long = 0
        var remainingData: Long = 0
        var allowedSms: Int = 0
        var remainingSms: Int = 0
        var allowedVoice: Int = 0
        var remainingVoice: Int = 0

        `in`.beginObject()
        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "iccid" -> iccid = `in`.nextString()
                "status" -> status = `in`.nextString()
                "startedAt" -> startedAt = `in`.nextLong()
                "expiredAt" -> expiredAt = `in`.nextLong()
                "allowedData" -> allowedData = `in`.nextLong()
                "remainingData" -> remainingData = `in`.nextLong()
                "allowedSms" -> allowedSms = `in`.nextInt()
                "remainingSms" -> remainingSms = `in`.nextInt()
                "allowedVoice" -> allowedVoice = `in`.nextInt()
                "remainingVoice" -> remainingVoice = `in`.nextInt()
                else -> `in`.skipValue()
            }
        }
        `in`.endObject()

        return UsageDetailsDto(
            iccid = iccid ?: "",
            esimStatus = status ?: "UNKNOWN",
            startedAt = startedAt,
            expiredAt = expiredAt,
            allowedData = allowedData,
            remainingData = remainingData,
            allowedSms = allowedSms,
            remainingSms = remainingSms,
            allowedVoice = allowedVoice,
            remainingVoice = remainingVoice
        )
    }
}
