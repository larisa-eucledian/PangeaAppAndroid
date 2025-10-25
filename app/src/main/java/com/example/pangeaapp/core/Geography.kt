package com.example.pangeaapp.core

import com.google.gson.annotations.SerializedName
import java.io.Serializable

enum class Geography : Serializable {
    @SerializedName("local") local,
    @SerializedName("regional") regional,
    @SerializedName("global") global
}