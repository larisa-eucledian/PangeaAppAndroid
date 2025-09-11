package com.example.pangeaapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pangeaapp.R
import com.example.pangeaapp.core.PackageRow
import com.example.pangeaapp.databinding.ItemPackageBinding

class PackageAdapter : ListAdapter<PackageRow, PackageAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<PackageRow>() {
        override fun areItemsTheSame(o: PackageRow, n: PackageRow) = o.documentId == n.documentId
        override fun areContentsTheSame(o: PackageRow, n: PackageRow) = o == n
        }

    inner class VH(val b: ItemPackageBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemPackageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = getItem(position)
        val ctx = holder.itemView.context

        holder.b.txtCountryName.text = p.countryName
        holder.b.txtPackageName.text = p.`package`

        val parts = mutableListOf<String>()
        parts += "${p.dataAmount} ${p.dataUnit}"
        if (p.withCall == true)    parts += ctx.getString(R.string.feature_calls)
        if (p.withSMS == true)     parts += ctx.getString(R.string.feature_sms)
        if (p.withHotspot == true) parts += ctx.getString(R.string.feature_hotspot)
        holder.b.txtPackageFeatures.text = parts.joinToString(" • ")

        val amount = java.lang.String.format(java.util.Locale.getDefault(), "%.2f", p.pricePublic)
        val currency = p.currency.orEmpty()
        holder.b.txtPackagePrice.text = if (currency.isNotEmpty()) "$currency $amount" else amount
    }
}