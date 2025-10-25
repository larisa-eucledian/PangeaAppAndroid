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

/**
 * CountriesViewModel maneja la lógica de CountriesFragment
 *
 * ACTUALIZADO para usar getCountriesFlow() en lugar de getCountries()
 */
@HiltViewModel
class CountriesViewModel @Inject constructor(
    private val plansRepository: PlansRepository
) : ViewModel() {

    enum class Mode { ONE, MULTIPLE }

    // Estados observables
    private val _countries = MutableStateFlow<List<CountryRow>>(emptyList())
    val countries: StateFlow<List<CountryRow>> = _countries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estados internos
    private var allCountries: List<CountryRow> = emptyList()
    private var currentMode = Mode.ONE
    private var currentQuery = ""

    /**
     * Carga países desde el repository usando Flow
     */
    fun loadCountries() {
        viewModelScope.launch {
            plansRepository.getCountriesFlow().collect { res ->
                // AGREGAR ESTE LOG:
                println("🔵 CountriesVM: ${res.javaClass.simpleName}")

                when (res) {
                    is Resource.Loading -> {
                        println("🟡 Loading...")
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        println("🟢 Success: ${res.data.size} countries")
                        _isLoading.value = false
                        allCountries = res.data
                        applyFilters()
                    }
                    is Resource.Error -> {
                        println("🔴 Error: ${res.message}")
                        _isLoading.value = false
                        allCountries = emptyList()
                        applyFilters()
                    }
                }
            }
        }
    }

    /**
     * Cambio de modo (One Country / Multiple Countries)
     */
    fun onModeChanged(mode: Mode) {
        currentMode = mode
        applyFilters()
    }

    /**
     * Cambio en búsqueda
     */
    fun onSearchQuery(query: String) {
        currentQuery = query
        applyFilters()
    }

    /**
     * Aplica filtros de modo y búsqueda
     */
    private fun applyFilters() {
        val locale = Locale.getDefault()
        println("🔵 Total countries: ${allCountries.size}")
        println("🔵 Current mode: $currentMode")
        println("🔵 First 3 geographies: ${allCountries.take(3).map { "${it.countryName}: ${it.geography}" }}")
        // Filtrar por modo
        val filteredByMode = when (currentMode) {
            Mode.ONE -> allCountries.filter { it.geography == Geography.local }
            Mode.MULTIPLE -> allCountries.filter {
                it.geography == Geography.regional || it.geography == Geography.global
            }
        }

        // Filtrar por búsqueda
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

    /**
     * Matching para países locales (One Country)
     */
    private fun matchesLocal(row: CountryRow, query: String, locale: Locale): Boolean {
        val q = query.trim()
        if (q.isEmpty()) return true

        // Buscar en nombre del país
        if (row.countryName.contains(q, ignoreCase = true)) return true

        // Buscar en región
        if (row.region?.contains(q, ignoreCase = true) == true) return true

        // Buscar en código de país
        if (row.countryCode.contains(q, ignoreCase = true)) return true

        // Buscar en nombre localizado
        val localized = displayNameForCode(row.countryCode.uppercase(), locale)
        return localized.contains(q, ignoreCase = true)
    }

    /**
     * Matching para regiones/global (Multiple Countries)
     */
    private fun matchesRegion(row: CountryRow, query: String, locale: Locale): Boolean {
        val q = query.trim()
        if (q.isEmpty()) return true

        // Buscar en nombre
        if (row.countryName.contains(q, ignoreCase = true)) return true

        // Buscar en código
        if (row.countryCode.contains(q, ignoreCase = true)) return true

        // Buscar en países cubiertos
        val covered = row.coveredCountries.orEmpty()
        if (covered.any { it.contains(q, ignoreCase = true) }) return true

        // Buscar en nombres localizados de países cubiertos
        if (covered.any { code ->
                displayNameForCode(code.uppercase(), locale).contains(q, ignoreCase = true)
            }) return true

        return false
    }

    /**
     * Obtiene el nombre localizado de un código de país
     */
    private fun displayNameForCode(code: String, locale: Locale): String {
        return try {
            Locale("", code).getDisplayCountry(locale)
        } catch (e: Exception) {
            ""
        }
    }
}