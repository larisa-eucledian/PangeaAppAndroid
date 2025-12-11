package com.example.pangeaapp.ui.esims

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.data.Resource
import com.example.pangeaapp.data.esim.ESimsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ESimsViewModel @Inject constructor(
    private val esimsRepository: ESimsRepository
) : ViewModel() {

    private val _esims = MutableStateFlow<List<ESimRow>>(emptyList())
    val esims: StateFlow<List<ESimRow>> = _esims.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadESims()
    }

    /**
     * Load eSIMs from repository (network-first strategy)
     */
    fun loadESims() {
        viewModelScope.launch {
            esimsRepository.getESimsFlow().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                        // Show cached data while loading fresh data
                        resource.data?.let { cachedData ->
                            _esims.value = sortESims(cachedData)
                        }
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        _esims.value = sortESims(resource.data)
                        _error.value = null
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        _error.value = resource.message
                    }
                }
            }
        }
    }

    /**
     * Manual refresh (invalidate cache and reload)
     */
    fun refresh() {
        viewModelScope.launch {
            esimsRepository.invalidateCache()
            loadESims()
        }
    }

    /**
     * Start retry polling after purchase
     * Waits 3 seconds then retries up to 5 times every 2 seconds
     */
    fun startRetryPollingAfterPurchase() {
        viewModelScope.launch {
            val initialCount = esims.value.size

            // Wait 3 seconds before first retry (give backend time to create eSIM)
            delay(3000)

            repeat(5) { attempt ->
                refresh()

                // Wait for flow to update
                delay(500)

                // Check if new eSIM appeared
                if (esims.value.size > initialCount) {
                    // New eSIM detected, stop polling
                    return@launch
                }

                // Wait 2 seconds before next retry
                delay(2000)
            }
        }
    }

    /**
     * Activate an eSIM
     */
    fun activateESim(esimId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = esimsRepository.activateESim(esimId)

            result.fold(
                onSuccess = {
                    // Refresh list to show updated status
                    refresh()
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Activation failed"
                    _isLoading.value = false
                }
            )
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Sort eSIMs by status (READY, INSTALLED, EXPIRED) then by creation date
     */
    private fun sortESims(esims: List<ESimRow>): List<ESimRow> {
        return esims.sortedWith(
            compareBy<ESimRow> { it.statusSortOrder }
                .thenByDescending { it.createdAt }
        )
    }
}
