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
    android.util.Log.d("NetworkBound", "Starting flow - emit Loading()")
    emit(Resource.Loading())

    android.util.Log.d("NetworkBound", "Getting cached data from query")
    val data = query().firstOrNull()
    android.util.Log.d("NetworkBound", "Cached data: $data")

    val flow = if (shouldFetch(data)) {
        android.util.Log.d("NetworkBound", "Should fetch = true, emit Loading(data)")
        emit(Resource.Loading(data))

        try {
            android.util.Log.d("NetworkBound", "Fetching from network...")
            val fetchedData = fetch()
            android.util.Log.d("NetworkBound", "Network fetch successful, saving...")
            saveFetchResult(fetchedData)
            android.util.Log.d("NetworkBound", "Saved, now querying DB again")
            query().map {
                android.util.Log.d("NetworkBound", "Mapping to Success: $it")
                Resource.Success(it)
            }
        } catch (throwable: Throwable) {
            android.util.Log.e("NetworkBound", "Network fetch failed: ${throwable.message}", throwable)
            onFetchFailed(throwable)
            query().map {
                android.util.Log.d("NetworkBound", "Mapping to Error with cached data: $it")
                Resource.Error(throwable.message ?: "Unknown error")
            }
        }
    } else {
        android.util.Log.d("NetworkBound", "Should fetch = false, using cached data")
        query().map {
            android.util.Log.d("NetworkBound", "Mapping cached to Success: $it")
            Resource.Success(it)
        }
    }

    android.util.Log.d("NetworkBound", "Emitting flow")
    emitAll(flow)
}