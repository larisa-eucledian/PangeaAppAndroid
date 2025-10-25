package com.example.pangeaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.pangeaapp.core.network.ConnectivityObserver
import com.example.pangeaapp.core.network.NetworkStatus
import com.example.pangeaapp.data.auth.SessionManager
import com.example.pangeaapp.databinding.ActivityMainBinding
import com.example.pangeaapp.ui.components.OfflineBanner
import com.example.pangeaapp.ui.theme.PangeaAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MainActivity - Single Activity Architecture
 *
 * Esta Activity es el único contenedor de la app.
 * Todos los "screens" son Fragments manejados por Navigation Component.
 *
 * ACTUALIZADO con verificación de sesión al inicio
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var connectivityObserver: ConnectivityObserver

    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupOfflineBanner()
        checkAuthState()
    }

    /**
     * Configura Navigation Component con BottomNavigationView
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Conectar BottomNavigationView con NavController
        binding.bottomNav.setupWithNavController(navController)

        // Listener para manejar navegación desde cualquier fragment
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    navController.navigate(R.id.countriesFragment)
                    true
                }
                R.id.nav_esims -> {
                    navController.navigate(R.id.esimsFragment)
                    true
                }
                R.id.nav_settings -> {
                    navController.navigate(R.id.settingsFragment)
                    true
                }
                else -> false
            }
        }

        // Listener para ocultar/mostrar bottom nav según el destino
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment,
                R.id.registerFragment -> {
                    // Ocultar bottom nav en pantallas de auth
                    binding.bottomNav.visibility = android.view.View.GONE
                }
                else -> {
                    // Mostrar bottom nav en el resto de pantallas
                    binding.bottomNav.visibility = android.view.View.VISIBLE
                }
            }
        }
    }

    /**
     * Configura Offline Banner usando Compose
     */
    private fun setupOfflineBanner() {
        binding.composeOfflineBanner.setContent {
            PangeaAppTheme {
                val networkStatus by connectivityObserver.observe()
                    .collectAsStateWithLifecycle(initialValue = NetworkStatus.Available)

                OfflineBanner(networkStatus = networkStatus)
            }
        }
    }

    /**
     * Verifica si el usuario está logueado
     * Si no, navega a LoginFragment
     */
    private fun checkAuthState() {
        lifecycleScope.launch {
            val isLoggedIn = sessionManager.hasValidSession()

            if (!isLoggedIn) {
                // Usuario NO está logueado → Ir a LoginFragment
                val navHostFragment = supportFragmentManager
                    .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController

                // Navegar a login y limpiar backstack
                navController.navigate(R.id.loginFragment)
            }
            // Si está logueado, se queda en el startDestination (countriesFragment)
        }
    }

    /**
     * Maneja el back button
     * Si estamos en un top-level destination (Explore, eSIMs, Settings), cierra la app
     * Si no, navega hacia atrás
     */
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}