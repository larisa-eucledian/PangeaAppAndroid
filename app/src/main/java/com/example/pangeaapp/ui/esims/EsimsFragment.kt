package com.example.pangeaapp.ui.esims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pangeaapp.databinding.FragmentEsimsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EsimsFragment : Fragment() {

    private var _b: FragmentEsimsBinding? = null
    private val b get() = _b!!

    private val viewModel: ESimsViewModel by viewModels()
    private lateinit var adapter: ESimAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _b = FragmentEsimsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSwipeRefresh()
        observeState()
    }

    private fun setupRecyclerView() {
        adapter = ESimAdapter { esim ->
            val action = EsimsFragmentDirections.actionEsimsToDetail(esim.esimId)
            findNavController().navigate(action)
        }

        b.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@EsimsFragment.adapter
        }
    }

    private fun setupSwipeRefresh() {
        b.swipeRefresh?.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Observe eSIMs
            launch {
                viewModel.esims.collect { esims ->
                    if (esims.isEmpty()) {
                        b.empty.visibility = View.VISIBLE
                        b.recycler.visibility = View.GONE
                    } else {
                        b.empty.visibility = View.GONE
                        b.recycler.visibility = View.VISIBLE
                        adapter.submitList(esims)
                    }
                }
            }

            // Observe loading state
            launch {
                viewModel.isLoading.collect { isLoading ->
                    b.swipeRefresh?.isRefreshing = isLoading
                }
            }

            // Observe errors
            launch {
                viewModel.error.collect { error ->
                    error?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
