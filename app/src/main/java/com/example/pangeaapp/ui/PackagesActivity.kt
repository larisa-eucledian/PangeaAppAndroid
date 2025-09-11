package com.example.pangeaapp.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pangeaapp.R
import com.example.pangeaapp.core.CountryArg
import com.example.pangeaapp.core.PackageRow
import com.example.pangeaapp.data.PlansRepository
import com.example.pangeaapp.databinding.ActivityPackagesBinding
import com.example.pangeaapp.di.AppDependencies
import kotlinx.coroutines.*

class PackagesActivity : AppCompatActivity() {

    companion object { const val EXTRA_COUNTRY_ARG = "EXTRA_COUNTRY_ARG" }

    private enum class PackageFilter { NONE, ONLY_DATA, DATA_CALLS, UNLIMITED }

    private lateinit var binding: ActivityPackagesBinding
    private lateinit var repo: PlansRepository
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    private lateinit var prefs: SharedPreferences
    private var currentFilter = PackageFilter.NONE

    private val adapter = PackageAdapter()
    private var all: List<PackageRow> = emptyList()
    private var filtered: List<PackageRow> = emptyList()

    private var countryCode: String? = null
    private var countryName: String? = null
    private var coverageCodes: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = AppDependencies.plansRepository
        prefs = getSharedPreferences("pangea_prefs", MODE_PRIVATE)

        val arg = intent.getParcelableExtra<CountryArg>(EXTRA_COUNTRY_ARG)
        countryCode = arg?.countryCode
        countryName = arg?.countryName
        coverageCodes = arg?.coverageCodes ?: emptyList()

        setSupportActionBar(binding.toolbarPackages)
        supportActionBar?.apply {
            title = getString(R.string.title_packages) + (countryName?.let { " - $it" } ?: "")
            setDisplayHomeAsUpEnabled(true)
        }

        binding.recyclerPackages.layoutManager = LinearLayoutManager(this)
        binding.recyclerPackages.adapter = adapter

        binding.edtSearchPackages.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters(s?.toString().orEmpty())
            }
        })

        // restaurar filtro
        currentFilter = when (prefs.getString("pkg_filter", "NONE")) {
            "ONLY_DATA" -> PackageFilter.ONLY_DATA
            "DATA_CALLS" -> PackageFilter.DATA_CALLS
            "UNLIMITED" -> PackageFilter.UNLIMITED
            else -> PackageFilter.NONE
        }
        when (currentFilter) {
            PackageFilter.ONLY_DATA -> binding.toggleFilters.check(R.id.btnOnlyData)
            PackageFilter.DATA_CALLS -> binding.toggleFilters.check(R.id.btnDataCalls)
            PackageFilter.UNLIMITED -> binding.toggleFilters.check(R.id.btnUnlimited)
            PackageFilter.NONE -> binding.toggleFilters.clearChecked()
        }

        binding.toggleFilters.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            currentFilter = when (checkedId) {
                R.id.btnOnlyData -> PackageFilter.ONLY_DATA
                R.id.btnDataCalls -> PackageFilter.DATA_CALLS
                R.id.btnUnlimited -> PackageFilter.UNLIMITED
                else -> PackageFilter.NONE
            }
            prefs.edit().putString("pkg_filter", currentFilter.name).apply()
            applyFilters(binding.edtSearchPackages.text?.toString().orEmpty())
        }

        uiScope.launch { loadPackages() }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private suspend fun loadPackages() {
        val allPkgs = repo.getPackages()

        val code = countryCode?.uppercase()
        val name = countryName

        val byName = name?.let { n ->
            allPkgs.filter { p -> p.countryName.equals(n, ignoreCase = true) }
        } ?: emptyList()

        val byCode = if (byName.isEmpty() && code != null) {
            allPkgs.filter { p -> p.coverage?.any { it.equals(code, ignoreCase = true) } == true }
        } else emptyList()

        all = if (byName.isNotEmpty()) byName else byCode
        filtered = all
        renderPackages(filtered)
    }

    private fun isUnlimited(p: PackageRow): Boolean =
        p.dataAmount.equals("unlimited", true) || p.dataAmount == "9007199254740991"

    private fun applyFilters(q: String) {
        var base = if (q.isBlank()) all else all.filter { it.`package`.contains(q, true) }

        base = when (currentFilter) {
            PackageFilter.ONLY_DATA   -> base.filter { !isUnlimited(it) && it.withCall != true }
            PackageFilter.DATA_CALLS  -> base.filter { !isUnlimited(it) && it.withCall == true }
            PackageFilter.UNLIMITED   -> base.filter { isUnlimited(it) }
            PackageFilter.NONE        -> base
        }

        filtered = base
        renderPackages(filtered)
    }

    private fun renderPackages(list: List<PackageRow>) {
        adapter.submitList(list)
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}
