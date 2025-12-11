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
                        // Don't navigate away, just reload the eSIM data
                        // The UI will update automatically via the Flow
                    }
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
        // Only show QR code if provided by backend (qrCodeUrl)
        // We don't generate QR codes locally since they can't be scanned from the same device
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

    private fun setupInstallButton(esim: com.example.pangeaapp.core.ESimRow) {
        // Show install button for INSTALLED (ACTIVE) eSIMs
        // User needs to physically install on device after backend activation
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
        // Build LPA code from eSIM data
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

        // Copy LPA code to clipboard
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.esim_clipboard_label), lpaString)
        clipboard.setPrimaryClip(clip)

        // Show dialog with instructions and button to open Settings
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.esim_install_dialog_title)
            .setMessage(getString(R.string.esim_install_instructions, lpaString))
            .setPositiveButton(R.string.esim_install_go_to_settings) { _, _ ->
                // Open wireless settings where user can add eSIM
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
