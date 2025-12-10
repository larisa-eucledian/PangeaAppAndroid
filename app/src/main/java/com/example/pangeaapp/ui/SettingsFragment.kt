package com.example.pangeaapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        view.findViewById<MaterialButton>(R.id.btnHelpVideo)?.setOnClickListener {
            openYouTubeVideo()
        }

        view.findViewById<MaterialButton>(R.id.btnWhatsAppSupport)?.setOnClickListener {
            openWhatsAppSupport()
        }

        view.findViewById<MaterialButton>(R.id.btnLogout)?.setOnClickListener {
            showLogoutConfirmation()
        }
    }

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

    private fun performLogout() {
        lifecycleScope.launch {
            sessionManager.logout()

            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun openYouTubeVideo() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.youtube_video_url)))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open video", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openWhatsAppSupport() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.whatsapp_support_url)))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }
}