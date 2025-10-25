package com.example.pangeaapp.ui.packages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.core.PackageRow
import com.example.pangeaapp.data.PlansRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * PackagesViewModel maneja la lógica de PackagesFragment
 *
 * ACTUALIZADO para usar getPackagesFlow() en lugar de getPackages()
 */
@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val plansRepository: PlansRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    enum class PackageFilter { NONE, ONLY_DATA, DATA_CALLS, UNLIMITED }

    // Estados observables
    private val _packages = MutableStateFlow<List<PackageRow>>(emptyList())
    val packages: StateFlow<List<PackageRow>> = _packages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estados internos
    private var allPackages: List<PackageRow> = emptyList()
    private var currentFilter = PackageFilter.NONE
    private var currentQuery = ""

    // SharedPreferences para persistir filtro
    private val prefs = context.getSharedPreferences("pangea_prefs", Context.MODE_PRIVATE)

    /**
     * Carga paquetes filtrados por país usando Flow
     */
    fun loadPackages(
        countryCode: String?,
        countryName: String?,
        coverageCodes: List<String>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Usar getPackagesFlow() en lugar de getPackages()
                plansRepository.getPackagesFlow().collect { allPkgs ->
                    // Filtrar por país/región
                    allPackages = filterByCountry(
                        packages = allPkgs as List<PackageRow>,
                        countryCode = countryCode,
                        countryName = countryName,
                        coverageCodes = coverageCodes
                    )

                    // Restaurar filtro guardado
                    currentFilter = getSavedFilter()

                    applyFilters()
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }

    /**
     * Cambio de filtro
     */
    fun onFilterChanged(filter: PackageFilter) {
        currentFilter = filter
        saveFilter(filter)
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
     * Obtiene filtro guardado
     */
    fun getSavedFilter(): PackageFilter {
        val filterName = prefs.getString("pkg_filter", "NONE") ?: "NONE"
        return try {
            PackageFilter.valueOf(filterName)
        } catch (e: Exception) {
            PackageFilter.NONE
        }
    }

    /**
     * Guarda filtro seleccionado
     */
    private fun saveFilter(filter: PackageFilter) {
        prefs.edit().putString("pkg_filter", filter.name).apply()
    }

    /**
     * Filtra paquetes por país/región
     */
    private fun filterByCountry(
        packages: List<PackageRow>,
        countryCode: String?,
        countryName: String?,
        coverageCodes: List<String>
    ): List<PackageRow> {
        val code = countryCode?.uppercase()

        // Primero intentar por nombre
        val byName = countryName?.let { name ->
            packages.filter { pkg ->
                pkg.countryName.equals(name, ignoreCase = true)
            }
        } ?: emptyList()

        // Si no hay por nombre, buscar por código
        val byCode = if (byName.isEmpty() && code != null) {
            packages.filter { pkg ->
                pkg.coverage?.any { it.equals(code, ignoreCase = true) } == true
            }
        } else emptyList()

        return if (byName.isNotEmpty()) byName else byCode
    }

    /**
     * Aplica filtros de tipo y búsqueda
     */
    private fun applyFilters() {
        // Filtrar por búsqueda
        var filtered = if (currentQuery.isBlank()) {
            allPackages
        } else {
            allPackages.filter { pkg ->
                pkg.packageName.contains(currentQuery, ignoreCase = true)
            }
        }

        // Filtrar por tipo
        filtered = when (currentFilter) {
            PackageFilter.ONLY_DATA -> filtered.filter {
                !isUnlimited(it) && it.withCall != true
            }
            PackageFilter.DATA_CALLS -> filtered.filter {
                !isUnlimited(it) && it.withCall == true
            }
            PackageFilter.UNLIMITED -> filtered.filter {
                isUnlimited(it)
            }
            PackageFilter.NONE -> filtered
        }

        _packages.value = filtered
    }

    /**
     * Determina si un paquete es ilimitado
     */
    private fun isUnlimited(pkg: PackageRow): Boolean {
        return pkg.dataAmount.equals("unlimited", ignoreCase = true) ||
                pkg.dataAmount == "9007199254740991"
    }
}