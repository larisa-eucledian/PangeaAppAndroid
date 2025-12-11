package com.example.pangeaapp.ui.esims

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
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
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
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
        binding.activateButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.esim_activate)
                .setMessage("Are you sure you want to activate this eSIM?")
                .setPositiveButton("Activate") { _, _ ->
                    viewModel.activateESim()
                }
                .setNegativeButton("Cancel", null)
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
                        findNavController().popBackStack()
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
        setupActivateButton(esim)
    }

    private fun displayQRCode(esim: com.example.pangeaapp.core.ESimRow) {
        val qrData = when {
            esim.qrCodeUrl != null && esim.qrCodeUrl.isNotEmpty() -> {
                binding.qrSection.visibility = View.VISIBLE
                loadQRFromUrl(esim.qrCodeUrl)
                return
            }
            esim.lpaCode != null && esim.lpaCode.isNotEmpty() -> esim.lpaCode
            esim.activationCode != null && esim.activationCode.isNotEmpty() -> {
                if (esim.smdpAddress != null) {
                    "LPA:1\$${esim.smdpAddress}\$${esim.activationCode}"
                } else {
                    esim.activationCode
                }
            }
            else -> null
        }

        if (qrData != null && esim.status == ESimStatus.READY_FOR_ACTIVATION) {
            binding.qrSection.visibility = View.VISIBLE
            generateQRCode(qrData)
        } else {
            binding.qrSection.visibility = View.GONE
        }
    }

    private fun loadQRFromUrl(url: String) {
        // For production, use Coil or Glide to load the image
        // For now, just show placeholder
        binding.qrCodeImage.setImageResource(android.R.drawable.ic_menu_gallery)
    }

    private fun generateQRCode(data: String) {
        try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }

            binding.qrCodeImage.setImageBitmap(bitmap)
        } catch (e: Exception) {
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
                    addInfoRow(getString(R.string.esim_info_created), formatDate(it))
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

    private fun setupActivateButton(esim: com.example.pangeaapp.core.ESimRow) {
        binding.activateButton.visibility = if (esim.status == ESimStatus.READY_FOR_ACTIVATION) {
            View.VISIBLE
        } else {
            View.GONE
        }
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
