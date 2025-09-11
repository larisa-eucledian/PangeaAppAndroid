package com.example.pangeaapp.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pangeaapp.R
import com.example.pangeaapp.core.CountryArg
import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.data.PlansRepository
import com.example.pangeaapp.databinding.ActivityCountriesBinding
import com.example.pangeaapp.di.AppDependencies
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

class CountriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountriesBinding
    private lateinit var repo: PlansRepository
    private val uiScope = CoroutineScope(Dispatchers.Main + Job())

    private val adapter = CountryAdapter { onCountrySelected(it) }
    private var allCountries: List<CountryRow> = emptyList()

    private enum class Mode { ONE, MULTIPLE }
    private var currentMode = Mode.ONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCountriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repo = AppDependencies.plansRepository

        setSupportActionBar(binding.toolbarCountries)
        supportActionBar?.title = getString(R.string.title_countries)

        binding.recyclerCountries.layoutManager = LinearLayoutManager(this)
        binding.recyclerCountries.adapter = adapter

        binding.edtSearchCountries.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCountries(s?.toString().orEmpty())
            }
        })

        binding.toggleMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            currentMode = when (checkedId) {
                R.id.btnSingle -> Mode.ONE
                R.id.btnMultiple -> Mode.MULTIPLE
                else -> Mode.ONE
            }
            filterCountries(binding.edtSearchCountries.text?.toString().orEmpty())
        }
        binding.toggleMode.check(R.id.btnSingle)


        loadCountries()
        }

    private fun loadCountries() {
        uiScope.launch {
            allCountries = repo.getCountries()
            renderCountries(filterByMode(allCountries))
        }
    }

    private fun renderCountries(list: List<CountryRow>) {
        adapter.submitList(list)
    }

    private fun filterCountries(q: String) {
        val loc = currentLocale()
        val base = when (currentMode) {
            Mode.ONE -> allCountries
                .filter { it.geography == Geography.local }
                .filter { matchesLocal(it, q, loc) }
            Mode.MULTIPLE -> allCountries
                .filter { it.geography == Geography.regional || it.geography == Geography.global }
                .filter { matchesRegion(it, q, loc) }
        }
        renderCountries(base)
    }


    private fun filterByMode(list: List<CountryRow>): List<CountryRow> {
        return when (currentMode) {
            Mode.ONE -> list.filter { it.geography == Geography.local }
            Mode.MULTIPLE -> list.filter {
                it.geography == Geography.regional || it.geography == Geography.global
            }
        }
    }

    private fun currentLocale(): Locale =
        resources.configuration.locales[0]

    private fun displayNameForCode(code: String, locale: Locale): String {
        return try { Locale("", code).getDisplayCountry(locale) } catch (_: Exception) { "" }
    }

    private fun matchesLocal(row: CountryRow, q: String, locale: Locale): Boolean {
        val nq = q.trim()
        if (nq.isEmpty()) return true
        if (row.countryName.contains(nq, ignoreCase = true)) return true
        if (row.region?.contains(nq, ignoreCase = true) == true) return true
        if (row.countryCode.contains(nq, ignoreCase = true)) return true
        val localized = displayNameForCode(row.countryCode.uppercase(), locale)
        if (localized.contains(nq, ignoreCase = true)) return true
        return false
    }

    private fun matchesRegion(row: CountryRow, q: String, locale: Locale): Boolean {
        val nq = q.trim()
        if (nq.isEmpty()) return true
        if (row.countryName.contains(nq, ignoreCase = true)) return true
        if (row.countryCode.contains(nq, ignoreCase = true)) return true
        val cov = row.coveredCountries.orEmpty()
        if (cov.any { it.contains(nq, ignoreCase = true) }) return true
        if (cov.any { displayNameForCode(it.uppercase(), locale).contains(nq, ignoreCase = true) }) return true
        return false
    }

    private fun onCountrySelected(c: CountryRow) {
        val arg = CountryArg(
            countryCode = c.countryCode,
            countryName = c.countryName,
            coverageCodes = ArrayList(c.coveredCountries ?: emptyList())
        )
        startActivity(
            Intent(this, PackagesActivity::class.java).putExtra(PackagesActivity.EXTRA_COUNTRY_ARG, arg)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel()
    }
}