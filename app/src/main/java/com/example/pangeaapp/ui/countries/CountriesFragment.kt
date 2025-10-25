package com.example.pangeaapp.ui.countries

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pangeaapp.R
import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.databinding.FragmentCountriesBinding
import com.example.pangeaapp.ui.CountryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * CountriesFragment muestra la lista de países/regiones disponibles
 *
 * Migrado de CountriesActivity → Ahora es Fragment
 */
@AndroidEntryPoint
class CountriesFragment : Fragment() {

    private var _binding: FragmentCountriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CountriesViewModel by viewModels()

    private val adapter = CountryAdapter { country -> onCountrySelected(country) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCountriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupToggle()
        observeViewModel()

        // Cargar países al inicio
        viewModel.loadCountries()
    }

    private fun setupRecyclerView() {
        binding.recyclerCountries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@CountriesFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.edtSearchCountries.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQuery(s?.toString().orEmpty())
            }
        })
    }

    private fun setupToggle() {
        // Iniciar en modo "One Country"
        binding.toggleMode.check(R.id.btnSingle)

        binding.toggleMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.btnSingle -> viewModel.onModeChanged(CountriesViewModel.Mode.ONE)
                R.id.btnMultiple -> viewModel.onModeChanged(CountriesViewModel.Mode.MULTIPLE)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.countries.collect { countries ->
                    adapter.submitList(countries)

                    // Mostrar empty state si no hay países
                    if (countries.isEmpty()) {
                        binding.emptyView.root.visibility = View.VISIBLE
                        binding.recyclerCountries.visibility = View.GONE
                    } else {
                        binding.emptyView.root.visibility = View.GONE
                        binding.recyclerCountries.visibility = View.VISIBLE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoading.collect { isLoading ->
                    // TODO: Mostrar loading indicator si es necesario
                }
            }
        }
    }

    private fun onCountrySelected(country: CountryRow) {
        // Navegar a PackagesFragment con los datos del país
        val action = CountriesFragmentDirections.actionCountriesToPackages(
            countryCode = country.countryCode,
            countryName = country.countryName,
            coverageCodes = country.coveredCountries?.toTypedArray() ?: emptyArray()
        )
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
