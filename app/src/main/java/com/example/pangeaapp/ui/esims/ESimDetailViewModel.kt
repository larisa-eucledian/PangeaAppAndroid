package com.example.pangeaapp.ui.esims

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.core.ESimStatus
import com.example.pangeaapp.core.ESimUsage
import com.example.pangeaapp.data.esim.ESimsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ESimDetailViewModel @Inject constructor(
    private val esimsRepository: ESimsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val esimId: String = savedStateHandle.get<String>("esimId")
        ?: throw IllegalStateException("esimId is required")

    private val _esim = MutableStateFlow<ESimRow?>(null)
    val esim: StateFlow<ESimRow?> = _esim.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _activationSuccess = MutableStateFlow(false)
    val activationSuccess: StateFlow<Boolean> = _activationSuccess.asStateFlow()

    private val _usage = MutableStateFlow<ESimUsage?>(null)
    val usage: StateFlow<ESimUsage?> = _usage.asStateFlow()

    init {
        loadESim()
    }

    private fun loadESim() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = esimsRepository.getESimById(esimId)
            _esim.value = result

            Log.d("ESimDetailVM", "=== eSIM Loaded: id=${result?.esimId}, status=${result?.status} ===")

            if (result == null) {
                _error.value = "eSIM not found"
                _isLoading.value = false
            } else {
                // Don't load package details - package may have changed since purchase
                // All package info is already in packageName field
                if (result.status == ESimStatus.INSTALLED) {
                    Log.d("ESimDetailVM", "eSIM is INSTALLED - calling loadUsage(${result.esimId})")
                    loadUsage(result.esimId)
                } else {
                    Log.d("ESimDetailVM", "eSIM status is ${result.status} - NOT calling loadUsage")
                }
                _isLoading.value = false
            }
        }
    }

    private fun loadUsage(esimId: String) {
        viewModelScope.launch {
            Log.d("ESimDetailVM", ">>> Calling API: getUsage($esimId)")
            esimsRepository.getUsage(esimId).fold(
                onSuccess = { usageData ->
                    Log.d("ESimDetailVM", "<<< Usage SUCCESS: dataConsumed=${usageData.dataConsumed}, remainingData=${usageData.remainingData}")
                    _usage.value = usageData
                },
                onFailure = { error ->
                    Log.e("ESimDetailVM", "<<< Usage FAILED: ${error.message}", error)
                }
            )
        }
    }

    fun activateESim() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val result = esimsRepository.activateESim(esimId)

            result.fold(
                onSuccess = { updatedEsim ->
                    _esim.value = updatedEsim
                    _activationSuccess.value = true
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _error.value = error.message ?: "Activation failed"
                    _isLoading.value = false
                }
            )
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearActivationSuccess() {
        _activationSuccess.value = false
    }
}
