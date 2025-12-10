package com.example.pangeaapp.ui.packages

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pangeaapp.R
import com.example.pangeaapp.databinding.FragmentPackagesBinding
import com.example.pangeaapp.ui.PackageAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PackagesFragment : Fragment() {

    private var _binding: FragmentPackagesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PackagesViewModel by viewModels()

    private val args: PackagesFragmentArgs by navArgs()

    private val adapter = PackageAdapter { packageRow ->
        navigateToCheckout(packageRow)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPackagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupFilters()
        observeViewModel()

        viewModel.loadPackages(
            countryCode = args.countryCode,
            countryName = args.countryName,
            coverageCodes = args.coverageCodes?.toList() ?: emptyList()
        )
    }

    private fun setupToolbar() {
        val title = if (!args.countryName.isNullOrEmpty()) {
            "${getString(R.string.title_packages)} - ${args.countryName}"
        } else {
            getString(R.string.title_packages)
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerPackages.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@PackagesFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.edtSearchPackages.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQuery(s?.toString().orEmpty())
            }
        })
    }

    private fun setupFilters() {
        val savedFilter = viewModel.getSavedFilter()
        when (savedFilter) {
            PackagesViewModel.PackageFilter.ONLY_DATA ->
                binding.toggleFilters.check(R.id.btnOnlyData)
            PackagesViewModel.PackageFilter.DATA_CALLS ->
                binding.toggleFilters.check(R.id.btnDataCalls)
            PackagesViewModel.PackageFilter.UNLIMITED ->
                binding.toggleFilters.check(R.id.btnUnlimited)
            PackagesViewModel.PackageFilter.NONE ->
                binding.toggleFilters.clearChecked()
        }

        binding.toggleFilters.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            val filter = when (checkedId) {
                R.id.btnOnlyData -> PackagesViewModel.PackageFilter.ONLY_DATA
                R.id.btnDataCalls -> PackagesViewModel.PackageFilter.DATA_CALLS
                R.id.btnUnlimited -> PackagesViewModel.PackageFilter.UNLIMITED
                else -> PackagesViewModel.PackageFilter.NONE
            }

            viewModel.onFilterChanged(filter)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.packages.collect { packages ->
                adapter.submitList(packages)

                if (packages.isEmpty()) {
                    binding.emptyView.root.visibility = View.VISIBLE
                    binding.recyclerPackages.visibility = View.GONE
                } else {
                    binding.emptyView.root.visibility = View.GONE
                    binding.recyclerPackages.visibility = View.VISIBLE
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                if (isLoading) {
                    binding.recyclerPackages.visibility = View.GONE
                    binding.emptyView.root.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateToCheckout(packageRow: com.example.pangeaapp.core.PackageRow) {
        val countryCode = args.countryCode ?: packageRow.coverage?.firstOrNull() ?: ""

        val callsLabel = if (packageRow.withCall == true) {
            packageRow.callAmount?.let { amount ->
                val unit = packageRow.callUnit ?: ""
                "$amount $unit".trim()
            } ?: getString(R.string.feature_calls)
        } else null

        val smsLabel = if (packageRow.withSMS == true) {
            packageRow.smsAmount?.let { amount ->
                val unit = packageRow.smsUnit ?: ""
                "$amount $unit".trim()
            } ?: getString(R.string.feature_sms)
        } else null

        val features = packageRow.featuresList().joinToString(" • ")
        val coverage = packageRow.coverage?.toTypedArray() ?: emptyArray()

        val action = PackagesFragmentDirections.actionPackagesToCheckout(
            packageId = packageRow.packageId,
            packageName = packageRow.packageName,
            countryName = packageRow.countryName,
            countryCode = countryCode,
            price = packageRow.pricePublic.toFloat(),
            validity = packageRow.validityDays,
            data = packageRow.dataLabel(),
            calls = callsLabel,
            sms = smsLabel,
            features = features,
            coverage = coverage
        )

        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
