package com.example.pangeaapp.data

import kotlinx.coroutines.flow.*

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
}

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType?) -> Boolean = { true },
    crossinline onFetchFailed: (Throwable) -> Unit = { }
) = flow {
    val data = query().firstOrNull()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            val fetchedData = fetch()
            saveFetchResult(fetchedData)
            query().map { Resource.Success(it) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.Error(throwable.message ?: "Unknown error") }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}
