package com.example.pangeaapp.ui.packages

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.core.PackageRow
import com.example.pangeaapp.data.PlansRepository
import com.example.pangeaapp.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackagesViewModel @Inject constructor(
    private val plansRepository: PlansRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    enum class PackageFilter { NONE, ONLY_DATA, DATA_CALLS, UNLIMITED }

    private val _packages = MutableStateFlow<List<PackageRow>>(emptyList())
    val packages: StateFlow<List<PackageRow>> = _packages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var allPackages: List<PackageRow> = emptyList()
    private var currentFilter = PackageFilter.NONE
    private var currentQuery = ""

    private val prefs = context.getSharedPreferences("pangea_prefs", Context.MODE_PRIVATE)

    fun loadPackages(
        countryCode: String?,
        countryName: String?,
        coverageCodes: List<String>
    ) {
        viewModelScope.launch {
            val code = countryCode ?: ""
            android.util.Log.d("PackagesVM", "Loading packages for country: $code")

            plansRepository.getPackagesByCountryFlow(code).collect { res ->
                android.util.Log.d("PackagesVM", "Resource received: ${res::class.simpleName}")
                when (res) {
                    is Resource.Loading -> {
                        android.util.Log.d("PackagesVM", "Loading state, cached data: ${res.data?.size ?: 0}")
                        _isLoading.value = true
                    }
                    is Resource.Success -> {
                        android.util.Log.d("PackagesVM", "Success! Packages: ${res.data.size}")
                        _isLoading.value = false

                        allPackages = res.data
                        android.util.Log.d("PackagesVM", "All packages set: ${allPackages.size}")

                        currentFilter = PackageFilter.NONE
                        applyFilters()
                    }
                    is Resource.Error -> {
                        android.util.Log.e("PackagesVM", "Error: ${res.message}")
                        _isLoading.value = false
                        allPackages = emptyList()
                        applyFilters()
                    }
                }
            }
        }
    }
    fun onFilterChanged(filter: PackageFilter) {
        currentFilter = filter
        saveFilter(filter)
        applyFilters()
    }

    fun onSearchQuery(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun getSavedFilter(): PackageFilter {
        val filterName = prefs.getString("pkg_filter", "NONE") ?: "NONE"
        return try {
            PackageFilter.valueOf(filterName)
        } catch (e: Exception) {
            PackageFilter.NONE
        }
    }

    private fun saveFilter(filter: PackageFilter) {
        prefs.edit().putString("pkg_filter", filter.name).apply()
    }

    private fun filterByCountry(
        packages: List<PackageRow>,
        countryCode: String?,
        countryName: String?,
        coverageCodes: List<String>
    ): List<PackageRow> {
        val code = countryCode?.uppercase()

        val byName = countryName?.let { name ->
            packages.filter { pkg ->
                pkg.countryName.equals(name, ignoreCase = true)
            }
        } ?: emptyList()

        val byCode = if (byName.isEmpty() && code != null) {
            packages.filter { pkg ->
                pkg.coverage?.any { it.equals(code, ignoreCase = true) } == true
            }
        } else emptyList()

        return if (byName.isNotEmpty()) byName else byCode
    }

    private fun applyFilters() {

        var filtered = if (currentQuery.isBlank()) {
            allPackages
        } else {
            allPackages.filter { pkg ->
                pkg.packageName.contains(currentQuery, ignoreCase = true)
            }
        }

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

    private fun isUnlimited(pkg: PackageRow): Boolean {
        return pkg.dataAmount.equals("unlimited", ignoreCase = true) ||
                pkg.dataAmount == "9007199254740991"
    }
}