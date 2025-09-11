package com.example.pangeaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pangeaapp.R
import com.example.pangeaapp.databinding.FragmentEsimsBinding

class EsimsFragment : Fragment() {
    private var _b: FragmentEsimsBinding? = null
    private val b get() = _b!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentEsimsBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        b.recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        b.empty.text = getString(R.string.empty_view)
        b.empty.visibility = View.VISIBLE
        b.recycler.visibility = View.GONE

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
