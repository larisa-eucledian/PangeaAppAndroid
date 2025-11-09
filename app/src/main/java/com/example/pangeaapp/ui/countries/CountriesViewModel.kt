package com.example.pangeaapp.ui.countries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.data.PlansRepository
import com.example.pangeaapp.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val plansRepository: PlansRepository
) : ViewModel() {

    enum class Mode { ONE, MULTIPLE }

    private val _countries = MutableStateFlow<List<CountryRow>>(emptyList())
    val countries: StateFlow<List<CountryRow>> = _countries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var allCountries: List<CountryRow> = emptyList()
    private var currentMode = Mode.ONE
    private var currentQuery = ""

    fun loadCountries() {
        viewModelScope.launch {
            plansRepository.getCountriesFlow().collect { res ->
                when (res) {
                    is Resource.Loading -> {
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        _isLoading.value = false
                        allCountries = res.data
                        applyFilters()
                    }
                    is Resource.Error -> {
                        _isLoading.value = false
                        allCountries = emptyList()
                        applyFilters()
                    }
                }
            }
        }
    }

    fun onModeChanged(mode: Mode) {
        currentMode = mode
        applyFilters()
    }

    fun onSearchQuery(query: String) {
        currentQuery = query
        applyFilters()
    }

    private fun applyFilters() {
        val locale = Locale.getDefault()
        val filteredByMode = when (currentMode) {
            Mode.ONE -> allCountries.filter { it.geography == Geography.local }
            Mode.MULTIPLE -> allCountries.filter {
                it.geography == Geography.regional || it.geography == Geography.global
            }
        }

        val filteredBySearch = if (currentQuery.isBlank()) {
            filteredByMode
        } else {
            filteredByMode.filter { country ->
                when (currentMode) {
                    Mode.ONE -> matchesLocal(country, currentQuery, locale)
                    Mode.MULTIPLE -> matchesRegion(country, currentQuery, locale)
                }
            }
        }

        _countries.value = filteredBySearch
    }

    private fun matchesLocal(row: CountryRow, query: String, locale: Locale): Boolean {
        val q = query.trim()
        if (q.isEmpty()) return true

        if (row.countryName.contains(q, ignoreCase = true)) return true

        if (row.region?.contains(q, ignoreCase = true) == true) return true

        if (row.countryCode.contains(q, ignoreCase = true)) return true

        val localized = displayNameForCode(row.countryCode.uppercase(), locale)
        return localized.contains(q, ignoreCase = true)
    }

    private fun matchesRegion(row: CountryRow, query: String, locale: Locale): Boolean {
        val q = query.trim()
        if (q.isEmpty()) return true

        if (row.countryName.contains(q, ignoreCase = true)) return true

        if (row.countryCode.contains(q, ignoreCase = true)) return true

        val covered = row.coveredCountries.orEmpty()
        if (covered.any { it.contains(q, ignoreCase = true) }) return true

        if (covered.any { code ->
                displayNameForCode(code.uppercase(), locale).contains(q, ignoreCase = true)
            }) return true

        return false
    }

    private fun displayNameForCode(code: String, locale: Locale): String {
        return try {
            Locale("", code).getDisplayCountry(locale)
        } catch (e: Exception) {
            ""
        }
    }
}