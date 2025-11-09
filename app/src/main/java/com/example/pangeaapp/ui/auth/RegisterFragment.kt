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
import com.example.pangeaapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val username = binding.edtUsername.text.toString().trim()
            val email = binding.edtEmail.text.toString().trim()
            val password = binding.edtPassword.text.toString()

            if (!validateInputs(username, email, password)) {
                return@setOnClickListener
            }

            viewModel.register(username, email, password)
        }

        binding.btnLoginLink.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun validateInputs(username: String, email: String, password: String): Boolean {
        binding.tilUsername.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        var isValid = true

        if (username.isEmpty()) {
            binding.tilUsername.error = getString(R.string.auth_error_required_field)
            isValid = false
        } else if (username.length < 3) {
            binding.tilUsername.error = getString(R.string.auth_error_username_short)
            isValid = false
        }

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(R.string.auth_error_required_field)
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = getString(R.string.auth_error_invalid_email)
            isValid = false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.auth_error_required_field)
            isValid = false
        } else if (password.length < 8) {
            binding.tilPassword.error = getString(R.string.auth_error_password_short)
            isValid = false
        }

        return isValid
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnRegister.isEnabled = false
                        binding.btnRegister.text = ""
                    }
                    is AuthViewModel.AuthState.Success -> {
                        Toast.makeText(
                            context,
                            getString(R.string.auth_register_success),
                            Toast.LENGTH_SHORT
                        ).show()

                        findNavController().navigate(R.id.action_register_to_countries)
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        binding.btnRegister.text = getString(R.string.auth_register)
                        
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                    is AuthViewModel.AuthState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.btnRegister.isEnabled = true
                        binding.btnRegister.text = getString(R.string.auth_register)
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