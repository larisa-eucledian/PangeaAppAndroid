package com.example.pangeaapp.ui.esims

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.core.ESimUsage
import com.example.pangeaapp.core.PackageRow
import com.example.pangeaapp.data.PlansRepository
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
    private val plansRepository: PlansRepository,
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

    private val _package = MutableStateFlow<PackageRow?>(null)
    val packageData: StateFlow<PackageRow?> = _package.asStateFlow()

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

            if (result == null) {
                _error.value = "eSIM not found"
                _isLoading.value = false
            } else {
                loadPackageDetails(result.packageId)
                // Only load usage for installed eSIMs
                if (result.status == com.example.pangeaapp.core.ESimStatus.INSTALLED) {
                    loadUsage(result.esimId)
                }
                _isLoading.value = false
            }
        }
    }

    private fun loadPackageDetails(packageId: String) {
        viewModelScope.launch {
            plansRepository.getPackageById(packageId).fold(
                onSuccess = { packageRow ->
                    _package.value = packageRow
                },
                onFailure = { /* Package details are optional */ }
            )
        }
    }

    private fun loadUsage(esimId: String) {
        viewModelScope.launch {
            esimsRepository.getUsage(esimId).fold(
                onSuccess = { usageData ->
                    _usage.value = usageData
                },
                onFailure = { /* Usage data is optional */ }
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
