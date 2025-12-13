package com.example.pangeaapp.ui.esims

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pangeaapp.R
import com.example.pangeaapp.core.ESimStatus
import com.example.pangeaapp.databinding.FragmentEsimDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import coil.load
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ESimDetailFragment : Fragment() {

    private var _binding: FragmentEsimDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ESimDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEsimDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.installButton.setOnClickListener {
            viewModel.esim.value?.let { esim ->
                installESim(esim)
            }
        }

        binding.activateButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.esim_activate_dialog_title)
                .setMessage(R.string.esim_activate_dialog_message)
                .setPositiveButton(R.string.esim_activate_dialog_confirm) { _, _ ->
                    viewModel.activateESim()
                }
                .setNegativeButton(R.string.esim_activate_dialog_cancel, null)
                .show()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
                viewModel.esim.collect { esim ->
                    esim?.let { displayESim(it) }
                }
            }

            launch {
                viewModel.isLoading.collect { isLoading ->
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }

            launch {
                viewModel.error.collect { error ->
                    error?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        viewModel.clearError()
                    }
                }
            }

            launch {
                viewModel.activationSuccess.collect { success ->
                    if (success) {
                        Toast.makeText(
                            requireContext(),
                            R.string.esim_activation_success,
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.clearActivationSuccess()
                    }
                }
            }

            launch {
                viewModel.usage.collect { usage ->
                    android.util.Log.d("ESimDetailFragment", "Usage collected: $usage")
                    usage?.let { displayUsageData(it) }
                }
            }
        }
    }

    private fun displayESim(esim: com.example.pangeaapp.core.ESimRow) {
        binding.countryFlag.text = if (esim.coverage.size == 1) {
            getFlagEmoji(esim.coverage[0])
        } else {
            "🌍"
        }

        binding.packageName.text = esim.packageName

        val (bgColor, textColor) = getStatusColors(esim.status)
        binding.statusBadge.setCardBackgroundColor(bgColor)
        binding.statusLabel.text = getStatusDisplayName(esim.status)
        binding.statusLabel.setTextColor(textColor)

        displayQRCode(esim)
        displayInfo(esim)
        setupInstallButton(esim)
        setupActivateButton(esim)
    }

    private fun displayQRCode(esim: com.example.pangeaapp.core.ESimRow) {
        if (esim.qrCodeUrl != null && esim.qrCodeUrl.isNotEmpty()) {
            binding.qrSection.visibility = View.VISIBLE
            binding.qrCodeImage.load(esim.qrCodeUrl) {
                crossfade(true)
                placeholder(android.R.drawable.ic_menu_gallery)
                error(android.R.drawable.ic_menu_close_clear_cancel)
            }
        } else {
            binding.qrSection.visibility = View.GONE
        }
    }

    private fun displayInfo(esim: com.example.pangeaapp.core.ESimRow) {
        binding.infoContainer.removeAllViews()

        if (esim.iccid != null && esim.iccid.isNotEmpty()) {
            addInfoRow(getString(R.string.esim_info_iccid), esim.iccid)
        }

        when (esim.status) {
            ESimStatus.INSTALLED -> {
                esim.activationDate?.let {
                    addInfoRow(getString(R.string.esim_activated_on, ""), formatDate(it))
                }
                esim.expirationDate?.let {
                    addInfoRow(getString(R.string.esim_expires_on, ""), formatDate(it))
                }
            }
            ESimStatus.READY_FOR_ACTIVATION -> {
                esim.createdAt?.let {
                    addInfoRow(getString(R.string.esim_info_purchased), formatDate(it))
                }
            }
            ESimStatus.EXPIRED -> {
                esim.expirationDate?.let {
                    addInfoRow(getString(R.string.esim_expired_on, ""), formatDate(it))
                }
            }
            else -> {}
        }

        if (esim.activationCode != null && esim.activationCode.isNotEmpty()) {
            addInfoRow(getString(R.string.esim_info_activation_code), esim.activationCode)
        }

        if (esim.smdpAddress != null && esim.smdpAddress.isNotEmpty()) {
            addInfoRow(getString(R.string.esim_info_smdp_address), esim.smdpAddress)
        }

        if (esim.lpaCode != null && esim.lpaCode.isNotEmpty()) {
            addInfoRow(getString(R.string.esim_info_lpa_code), esim.lpaCode)
        }

        if (esim.coverage.isNotEmpty()) {
            val coverageText = esim.coverage.joinToString(", ") { code ->
                "${getFlagEmoji(code)} ${getCountryName(code)}"
            }
            addInfoRow(getString(R.string.esim_info_coverage), coverageText)
        }
    }

    private fun addInfoRow(label: String, value: String) {
        val row = LayoutInflater.from(requireContext())
            .inflate(android.R.layout.simple_list_item_2, binding.infoContainer, false)

        row.findViewById<TextView>(android.R.id.text1).apply {
            text = label
            textSize = 12f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.textMuted))
        }

        row.findViewById<TextView>(android.R.id.text2).apply {
            text = value
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        }

        binding.infoContainer.addView(row)
    }

    private fun displayUsageData(usage: com.example.pangeaapp.core.ESimUsage) {
        android.util.Log.d("ESimDetailFragment", "=== displayUsageData called ===")
        android.util.Log.d("ESimDetailFragment", "dataConsumed: ${usage.dataConsumed}, allowedData: ${usage.allowedData}")

        binding.usageContainer.removeAllViews()

        // Convert bytes to GB (matches iOS)
        val dataConsumedGB = usage.dataConsumed / 1_000_000_000.0
        val dataAllowedGB = usage.allowedData / 1_000_000_000.0
        val dataPercentage = usage.dataUsagePercentage

        android.util.Log.d("ESimDetailFragment", "Displaying: $dataConsumedGB GB / $dataAllowedGB GB ($dataPercentage%)")

        // Data usage - Format: "0.50 GB / 1.00 GB (50%)"
        addUsageRow(
            getString(R.string.esim_usage_data),
            String.format("%.2f GB / %.2f GB (%d%%)", dataConsumedGB, dataAllowedGB, dataPercentage)
        )

        // SMS usage - Format: "150 / 500"
        if (usage.allowedSms > 0) {
            addUsageRow(
                getString(R.string.esim_usage_sms),
                "${usage.remainingSms} / ${usage.allowedSms}"
            )
        }

        // Voice usage - Format: "180 / 300 min"
        if (usage.allowedVoice > 0) {
            addUsageRow(
                getString(R.string.esim_usage_voice),
                "${usage.remainingVoice} / ${usage.allowedVoice} min"
            )
        }

        android.util.Log.d("ESimDetailFragment", "Setting usageCard visibility to VISIBLE")
        binding.usageCard.visibility = View.VISIBLE
    }

    private fun addUsageRow(title: String, value: String) {
        // Simple horizontal layout (matches iOS)
        val row = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        val titleLabel = TextView(requireContext()).apply {
            text = title
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }
        row.addView(titleLabel)

        val valueLabel = TextView(requireContext()).apply {
            text = value
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            gravity = android.view.Gravity.END
        }
        row.addView(valueLabel)

        binding.usageContainer.addView(row)
    }

    private fun formatDataAmount(bytes: Long): String {
        return when {
            bytes >= 1_000_000_000 -> String.format("%.1f GB", bytes / 1_000_000_000.0)
            bytes >= 1_000_000 -> String.format("%.1f MB", bytes / 1_000_000.0)
            else -> String.format("%.1f KB", bytes / 1_000.0)
        }
    }

    private fun setupInstallButton(esim: com.example.pangeaapp.core.ESimRow) {
        binding.installButton.visibility = if (esim.status == ESimStatus.INSTALLED) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun setupActivateButton(esim: com.example.pangeaapp.core.ESimRow) {
        binding.activateButton.visibility = if (esim.status == ESimStatus.READY_FOR_ACTIVATION) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    private fun installESim(esim: com.example.pangeaapp.core.ESimRow) {
        val lpaString = when {
            esim.lpaCode != null && esim.lpaCode.isNotEmpty() -> esim.lpaCode
            esim.activationCode != null && esim.smdpAddress != null -> {
                "LPA:1\$${esim.smdpAddress}\$${esim.activationCode}"
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.esim_error_missing_activation_data),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.esim_clipboard_label), lpaString)
        clipboard.setPrimaryClip(clip)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.esim_install_dialog_title)
            .setMessage(getString(R.string.esim_install_instructions, lpaString))
            .setPositiveButton(R.string.esim_install_go_to_settings) { _, _ ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()

        Toast.makeText(
            requireContext(),
            R.string.esim_code_copied,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getFlagEmoji(countryCode: String): String {
        if (countryCode.length != 2) return ""

        val firstLetter = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6
        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    private fun getCountryName(countryCode: String): String {
        if (countryCode.length != 2) return countryCode

        return try {
            val locale = Locale.Builder()
                .setRegion(countryCode.uppercase())
                .build()
            locale.displayCountry
        } catch (e: Exception) {
            countryCode.uppercase()
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateStr)

            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun getStatusColors(status: ESimStatus): Pair<Int, Int> {
        return when (status) {
            ESimStatus.READY_FOR_ACTIVATION -> Pair(
                ContextCompat.getColor(requireContext(), R.color.status_ready_bg),
                ContextCompat.getColor(requireContext(), R.color.status_ready_text)
            )
            ESimStatus.INSTALLED -> Pair(
                ContextCompat.getColor(requireContext(), R.color.status_installed_bg),
                ContextCompat.getColor(requireContext(), R.color.status_installed_text)
            )
            ESimStatus.EXPIRED -> Pair(
                ContextCompat.getColor(requireContext(), R.color.status_expired_bg),
                ContextCompat.getColor(requireContext(), R.color.status_expired_text)
            )
            ESimStatus.UNKNOWN -> Pair(
                ContextCompat.getColor(requireContext(), R.color.status_unknown_bg),
                ContextCompat.getColor(requireContext(), R.color.status_unknown_text)
            )
        }
    }

    private fun getStatusDisplayName(status: ESimStatus): String {
        return when (status) {
            ESimStatus.READY_FOR_ACTIVATION -> getString(R.string.esim_status_ready)
            ESimStatus.INSTALLED -> getString(R.string.esim_status_active)
            ESimStatus.EXPIRED -> getString(R.string.esim_status_expired)
            ESimStatus.UNKNOWN -> getString(R.string.esim_status_unknown)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
