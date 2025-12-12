package com.example.pangeaapp.ui.esims

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pangeaapp.R
import com.example.pangeaapp.core.ESimRow
import com.example.pangeaapp.core.ESimStatus
import com.example.pangeaapp.databinding.ItemEsimBinding
import java.text.SimpleDateFormat
import java.util.*

class ESimAdapter(
    private val onItemClick: (ESimRow) -> Unit
) : ListAdapter<ESimRow, ESimAdapter.ESimViewHolder>(ESimDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ESimViewHolder {
        val binding = ItemEsimBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ESimViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ESimViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ESimViewHolder(
        private val binding: ItemEsimBinding,
        private val onItemClick: (ESimRow) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(esim: ESimRow) {
            // Click listener
            binding.root.setOnClickListener {
                onItemClick(esim)
            }

            // Flag emoji
            binding.countryFlag.text = if (esim.coverage.size == 1) {
                getFlagEmoji(esim.coverage[0])
            } else {
                "🌍"  // Globe for multi-country
            }

            // Package name
            binding.packageName.text = esim.packageName

            // Status badge
            val (bgColor, textColor) = getStatusColors(binding.root.context, esim.status)
            binding.statusBadge.setCardBackgroundColor(bgColor)
            binding.statusLabel.text = getStatusDisplayName(binding.root.context, esim.status)
            binding.statusLabel.setTextColor(textColor)

            // Info and CTA based on status
            when (esim.status) {
                ESimStatus.INSTALLED -> configureInstalled(esim)
                ESimStatus.READY_FOR_ACTIVATION -> configureReady(esim)
                ESimStatus.EXPIRED -> configureExpired(esim)
                ESimStatus.UNKNOWN -> configureUnknown(esim)
            }
        }

        private fun configureInstalled(esim: ESimRow) {
            val context = binding.root.context
            val infoBuilder = StringBuilder()

            // Activation date
            esim.activationDate?.let { dateStr ->
                val formatted = formatDate(dateStr)
                infoBuilder.append(context.getString(R.string.esim_activated_on, formatted))
            }

            // Expiration date
            esim.expirationDate?.let { dateStr ->
                if (infoBuilder.isNotEmpty()) infoBuilder.append("\n")
                val formatted = formatDate(dateStr)
                infoBuilder.append(context.getString(R.string.esim_expires_on, formatted))
            }

            // ICCID
            esim.iccid?.let { iccid ->
                if (iccid.isNotEmpty()) {
                    if (infoBuilder.isNotEmpty()) infoBuilder.append("\n")
                    infoBuilder.append("ICCID: $iccid")
                }
            }

            binding.infoText.text = infoBuilder.toString()
            binding.infoText.visibility = View.VISIBLE
            binding.ctaText.text = context.getString(R.string.esim_tap_to_check_usage)
            binding.ctaText.visibility = View.VISIBLE
        }

        private fun configureReady(esim: ESimRow) {
            val context = binding.root.context
            val infoBuilder = StringBuilder()

            // Purchase date (createdAt)
            esim.createdAt?.let { dateStr ->
                val formatted = formatDate(dateStr)
                infoBuilder.append(context.getString(R.string.esim_purchased_on, formatted))
            }

            binding.infoText.text = infoBuilder.toString()
            binding.infoText.visibility = View.VISIBLE
            binding.ctaText.text = context.getString(R.string.esim_tap_to_activate)
            binding.ctaText.visibility = View.VISIBLE
        }

        private fun configureExpired(esim: ESimRow) {
            val context = binding.root.context
            val infoBuilder = StringBuilder()

            // Expiration date
            esim.expirationDate?.let { dateStr ->
                val formatted = formatDate(dateStr)
                infoBuilder.append(context.getString(R.string.esim_expired_on, formatted))
            }

            binding.infoText.text = infoBuilder.toString()
            binding.infoText.visibility = View.VISIBLE
            binding.ctaText.text = context.getString(R.string.esim_tap_to_view_details)
            binding.ctaText.visibility = View.VISIBLE
        }

        private fun configureUnknown(esim: ESimRow) {
            binding.infoText.text = ""
            binding.infoText.visibility = View.GONE
            binding.ctaText.text = ""
            binding.ctaText.visibility = View.GONE
        }

        /**
         * Get flag emoji from country code
         */
        private fun getFlagEmoji(countryCode: String): String {
            if (countryCode.length != 2) return ""

            val firstLetter = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6
            return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        }

        /**
         * Format ISO date string to readable format
         */
        private fun formatDate(dateStr: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val date = inputFormat.parse(dateStr)

                val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                date?.let { outputFormat.format(it) } ?: dateStr
            } catch (e: Exception) {
                // Fallback: just return the string as-is
                dateStr
            }
        }

        /**
         * Get status colors for background and text
         */
        private fun getStatusColors(context: Context, status: ESimStatus): Pair<Int, Int> {
            return when (status) {
                ESimStatus.READY_FOR_ACTIVATION -> Pair(
                    ContextCompat.getColor(context, R.color.status_ready_bg),
                    ContextCompat.getColor(context, R.color.status_ready_text)
                )
                ESimStatus.INSTALLED -> Pair(
                    ContextCompat.getColor(context, R.color.status_installed_bg),
                    ContextCompat.getColor(context, R.color.status_installed_text)
                )
                ESimStatus.EXPIRED -> Pair(
                    ContextCompat.getColor(context, R.color.status_expired_bg),
                    ContextCompat.getColor(context, R.color.status_expired_text)
                )
                ESimStatus.UNKNOWN -> Pair(
                    ContextCompat.getColor(context, R.color.status_unknown_bg),
                    ContextCompat.getColor(context, R.color.status_unknown_text)
                )
            }
        }

        /**
         * Get localized status display name
         */
        private fun getStatusDisplayName(context: Context, status: ESimStatus): String {
            return when (status) {
                ESimStatus.READY_FOR_ACTIVATION -> context.getString(R.string.esim_status_ready)
                ESimStatus.INSTALLED -> context.getString(R.string.esim_status_active)
                ESimStatus.EXPIRED -> context.getString(R.string.esim_status_expired)
                ESimStatus.UNKNOWN -> context.getString(R.string.esim_status_unknown)
            }
        }
    }

    private class ESimDiffCallback : DiffUtil.ItemCallback<ESimRow>() {
        override fun areItemsTheSame(oldItem: ESimRow, newItem: ESimRow): Boolean {
            return oldItem.esimId == newItem.esimId
        }

        override fun areContentsTheSame(oldItem: ESimRow, newItem: ESimRow): Boolean {
            return oldItem == newItem
        }
    }
}
