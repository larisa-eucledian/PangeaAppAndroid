package com.example.pangeaapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pangeaapp.R
import com.example.pangeaapp.data.auth.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * SettingsFragment - Pantalla de configuración
 *
 * Por ahora solo tiene Logout, pero puedes agregar más opciones después
 */
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Botón de logout
        view.findViewById<MaterialButton>(R.id.btnLogout)?.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    /**
     * Muestra confirmación antes de hacer logout
     */
    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Ejecuta el logout
     */
    private fun performLogout() {
        lifecycleScope.launch {
            // Limpiar sesión
            sessionManager.logout()

            // Navegar a login y limpiar backstack
            findNavController().navigate(R.id.loginFragment)
        }
    }
}