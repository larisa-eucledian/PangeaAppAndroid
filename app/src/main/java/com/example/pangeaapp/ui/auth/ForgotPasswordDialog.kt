package com.example.pangeaapp.ui.auth

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.pangeaapp.R
import com.example.pangeaapp.databinding.DialogForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * ForgotPasswordDialog - Dialog para solicitar reset de contraseña
 */
@AndroidEntryPoint
class ForgotPasswordDialog : DialogFragment() {

    private var _binding: DialogForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogForgotPasswordBinding.inflate(LayoutInflater.from(context))

        setupListeners()

        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    private fun setupListeners() {
        // Botón cancelar
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // Botón enviar
        binding.btnSend.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()

            // Validar email
            if (email.isEmpty()) {
                binding.tilEmail.error = getString(R.string.auth_error_required_field)
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = getString(R.string.auth_error_invalid_email)
                return@setOnClickListener
            }

            // Limpiar error
            binding.tilEmail.error = null

            // Deshabilitar botón mientras procesa
            binding.btnSend.isEnabled = false

            // Llamar al ViewModel
            viewModel.forgotPassword(email) {
                // Callback de éxito
                Toast.makeText(
                    context,
                    getString(R.string.auth_email_sent),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ForgotPasswordDialog"

        fun newInstance(): ForgotPasswordDialog {
            return ForgotPasswordDialog()
        }
    }
}