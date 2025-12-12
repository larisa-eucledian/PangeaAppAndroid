package com.example.pangeaapp.ui.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pangeaapp.data.transaction.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun createPaymentIntent(amount: Double, packageId: String) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = transactionRepository.createStripeTransaction(
                amount = amount,
                packageId = packageId
            )

            result.fold(
                onSuccess = { response ->
                    _paymentState.value = PaymentState.Ready(
                        clientSecret = response.clientSecret,
                        paymentIntentId = response.paymentIntentId
                    )
                    _isLoading.value = false
                },
                onFailure = { error ->
                    _paymentState.value = PaymentState.Error(
                        error.message ?: "Unknown error"
                    )
                    _isLoading.value = false
                }
            )
        }
    }

    fun onPaymentCompleted(success: Boolean) {
        _paymentState.value = if (success) {
            PaymentState.Success
        } else {
            PaymentState.Error("Payment failed")
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

    sealed class PaymentState {
        object Idle : PaymentState()
        data class Ready(
            val clientSecret: String,
            val paymentIntentId: String
        ) : PaymentState()
        object Success : PaymentState()
        data class Error(val message: String) : PaymentState()
    }
}
