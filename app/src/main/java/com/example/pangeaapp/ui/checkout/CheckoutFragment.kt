package com.example.pangeaapp.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pangeaapp.R
import com.example.pangeaapp.databinding.FragmentCheckoutBinding
import com.google.android.material.chip.Chip
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckoutViewModel by viewModels()
    private val args: CheckoutFragmentArgs by navArgs()

    private lateinit var paymentSheet: PaymentSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPaymentSheet()
        setupUI()
        observeViewModel()
    }

    private fun setupPaymentSheet() {
        paymentSheet = PaymentSheet(this) { result ->
            handlePaymentResult(result)
        }
    }

    private fun setupUI() {
        binding.txtCountryFlag.text = getCountryFlagEmoji(args.countryCode)
        binding.txtCountryName.text = args.countryName
        binding.txtPlanTitle.text = args.packageName
        binding.txtPlanSubtitle.text = when {
            args.calls != null && args.sms != null ->
                getString(R.string.filter_data_calls)
            args.calls != null ->
                getString(R.string.filter_data_calls)
            else ->
                getString(R.string.filter_only_data)
        }
        binding.txtPrice.text = getString(R.string.checkout_price_mxn, args.price)

        binding.txtValidity.text = getString(
            R.string.checkout_validity_days,
            args.validity
        )
        binding.txtData.text = args.data

        args.calls?.let { calls ->
            binding.layoutCalls.visibility = View.VISIBLE
            binding.txtCalls.text = calls
        }

        args.sms?.let { sms ->
            binding.layoutSms.visibility = View.VISIBLE
            binding.txtSms.text = sms
        }

        binding.txtFeatures.text = args.features

        args.coverage.forEach { countryCode ->
            val chip = Chip(requireContext()).apply {
                val flag = getCountryFlagEmoji(countryCode)
                val name = getCountryName(countryCode)
                text = "$flag $name"
                isClickable = false
                isCheckable = false
            }
            binding.chipGroupCoverage.addView(chip)
        }

        val payButtonText = getString(
            R.string.checkout_pay,
            getString(R.string.checkout_price_mxn, args.price)
        )
        binding.btnPay.text = payButtonText
        binding.btnPay.setOnClickListener {
            viewModel.createPaymentIntent(args.price.toDouble(), args.packageId)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnPay.isEnabled = !isLoading
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.paymentState.collect { state ->
                when (state) {
                    is CheckoutViewModel.PaymentState.Ready -> {
                        presentPaymentSheet(state.clientSecret)
                    }
                    is CheckoutViewModel.PaymentState.Success -> {
                        handlePaymentSuccess()
                    }
                    is CheckoutViewModel.PaymentState.Error -> {
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    is CheckoutViewModel.PaymentState.Idle -> {
                    }
                }
            }
        }
    }

    private fun presentPaymentSheet(clientSecret: String) {
        val configuration = PaymentSheet.Configuration(
            merchantDisplayName = getString(R.string.app_name)
        )

        paymentSheet.presentWithPaymentIntent(
            clientSecret,
            configuration
        )
    }

    private fun handlePaymentResult(result: PaymentSheetResult) {
        when (result) {
            is PaymentSheetResult.Completed -> {
                viewModel.onPaymentCompleted(true)
            }
            is PaymentSheetResult.Canceled -> {
                viewModel.resetPaymentState()
            }
            is PaymentSheetResult.Failed -> {
                viewModel.onPaymentCompleted(false)
                Toast.makeText(
                    requireContext(),
                    result.error.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handlePaymentSuccess() {
        Toast.makeText(
            requireContext(),
            R.string.payment_success,
            Toast.LENGTH_SHORT
        ).show()

        // Navigate to eSIMs and trigger polling for new eSIM
        findNavController().navigate(R.id.esimsFragment)
        findNavController().currentBackStackEntry?.savedStateHandle?.set("start_polling", true)
    }

    private fun getCountryFlagEmoji(countryCode: String): String {
        if (countryCode.length != 2) return ""

        val firstLetter = Character.codePointAt(countryCode.uppercase(), 0) - 0x41 + 0x1F1E6
        val secondLetter = Character.codePointAt(countryCode.uppercase(), 1) - 0x41 + 0x1F1E6

        return String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
    }

    private fun getCountryName(countryCode: String): String {
        if (countryCode.length != 2) return countryCode

        return try {
            val locale = java.util.Locale.Builder()
                .setRegion(countryCode.uppercase())
                .build()
            locale.displayCountry
        } catch (e: Exception) {
            countryCode.uppercase()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
