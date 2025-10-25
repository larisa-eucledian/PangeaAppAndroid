package com.example.pangeaapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pangeaapp.R
import com.example.pangeaapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * LoginFragment - Pantalla de inicio de sesión
 */
@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // Botón de login
        binding.btnLogin.setOnClickListener {
            val identifier = binding.edtIdentifier.text.toString().trim()
            val password = binding.edtPassword.text.toString()

            // Validación local
            if (!validateInputs(identifier, password)) {
                return@setOnClickListener
            }

            viewModel.login(identifier, password)
        }

        // Link para ir a registro
        binding.btnRegisterLink.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }

        // Forgot password dialog
        binding.txtForgotPassword.setOnClickListener {
            ForgotPasswordDialog.newInstance()
                .show(childFragmentManager, ForgotPasswordDialog.TAG)
        }
    }

    /**
     * Validación local de inputs
     */
    private fun validateInputs(identifier: String, password: String): Boolean {
        // Limpiar errores anteriores
        binding.tilIdentifier.error = null
        binding.tilPassword.error = null

        var isValid = true

        // Validar identifier
        if (identifier.isEmpty()) {
            binding.tilIdentifier.error = getString(R.string.auth_error_required_field)
            isValid = false
        }

        // Validar password
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.auth_error_required_field)
            isValid = false
        }

        return isValid
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnLogin.isEnabled = false
                        binding.btnLogin.text = ""
                    }
                    is AuthViewModel.AuthState.Success -> {
                        Toast.makeText(
                            context,
                            getString(R.string.auth_login_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar a countries
                        findNavController().navigate(R.id.action_login_to_countries)
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.auth_login)

                        // Mostrar error
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthViewModel.AuthState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnLogin.isEnabled = true
                        binding.btnLogin.text = getString(R.string.auth_login)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}