package com.example.pangeaapp.core.network

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

class DataUnwrapTypeAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T> {
        val delegate: TypeAdapter<T> = gson.getDelegateAdapter(this, type)
        val elementAdapter: TypeAdapter<JsonElement> = gson.getAdapter(JsonElement::class.java)

        // Skip unwrapping for types that already handle the wrapper themselves
        val typeName = type.rawType.simpleName
        val skipUnwrap = typeName.endsWith("ResponseDto") || typeName.endsWith("Response")

        return object : TypeAdapter<T>() {
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            override fun read(`in`: JsonReader): T {
                val jsonElement = elementAdapter.read(`in`)

                // Skip unwrapping for response DTOs that include their own wrapper
                val targetElement = if (skipUnwrap) {
                    jsonElement
                } else if (jsonElement.isJsonObject) {
                    val obj = jsonElement.asJsonObject
                    if (obj.has("data")) obj.get("data") else jsonElement
                } else {
                    jsonElement
                }

                return delegate.fromJsonTree(targetElement)
            }
        }
    }
}
