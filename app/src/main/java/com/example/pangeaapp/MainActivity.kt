package com.example.pangeaapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pangeaapp.core.network.ConnectivityObserver
import com.example.pangeaapp.core.network.NetworkStatus
import com.example.pangeaapp.databinding.ActivityMainBinding
import com.example.pangeaapp.ui.components.OfflineBanner
import com.example.pangeaapp.ui.theme.PangeaAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setupWithNavController(navController)

        // Offline Banner usando Compose
        binding.composeOfflineBanner.setContent {
            PangeaAppTheme {
                val networkStatus by connectivityObserver.observe()
                    .collectAsStateWithLifecycle(initialValue = NetworkStatus.Available)

                OfflineBanner(networkStatus = networkStatus)
            }
        }
    }
}