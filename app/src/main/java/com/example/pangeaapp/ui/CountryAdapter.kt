package com.example.pangeaapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.layout.Layout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.pangeaapp.R
import com.example.pangeaapp.core.CountryRow
import com.example.pangeaapp.core.Geography
import com.example.pangeaapp.databinding.ItemCountryBinding

class CountryAdapter (
    private val onClick: (CountryRow) -> Unit
) : ListAdapter<CountryRow, CountryAdapter.VH>(Diff){

    object Diff : DiffUtil.ItemCallback<CountryRow>() {
        override fun areItemsTheSame(o: CountryRow, n: CountryRow) = o.documentId == n.documentId
        override fun areContentsTheSame(o: CountryRow, n: CountryRow) = o == n
        }

    inner class VH(val b: ItemCountryBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.b.txtCountryName.text = item.countryName
        val n = item.coveredCountries?.size ?: 0
        holder.b.txtCountrySubtitle.text = when (item.geography) {
            com.example.pangeaapp.core.Geography.local -> item.region ?: ""
            com.example.pangeaapp.core.Geography.regional,
            com.example.pangeaapp.core.Geography.global ->
                holder.itemView.context.resources.getQuantityString(
                    com.example.pangeaapp.R.plurals.countries_count, n, n
                )
        }

        holder.b.imgFlag.load(item.imageUrl) {
            placeholder(com.example.pangeaapp.R.drawable.flag_placeholder)
            error(com.example.pangeaapp.R.drawable.flag_placeholder)
            crossfade(true)
        }

        holder.itemView.setOnClickListener { onClick(item) }
    }
}