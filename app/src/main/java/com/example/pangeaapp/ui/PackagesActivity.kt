package com.example.pangeaapp.ui

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

    private lateinit var binding: ActivityPackagesBinding
    private lateinit var repo: PlansRepository
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    private val adapter = PackageAdapter()
    private var all: List<PackageRow> = emptyList()
    private var filtered: List<PackageRow> = emptyList()

    // ← usa propiedades (no locales) para que loadPackages las vea
    private var countryCode: String? = null
    private var countryName: String? = null
    private var coverageCodes: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = AppDependencies.plansRepository

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
        adapter.submitList(filtered)
    }

    private fun applyFilters(q: String) {
        filtered = if (q.isBlank()) all
        else all.filter { p -> p.`package`.contains(q, true) }
        adapter.submitList(filtered)
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}