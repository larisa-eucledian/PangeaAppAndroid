package com.example.pangeaapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pangeaapp.R
import com.example.pangeaapp.databinding.ActivityEsimsSettingsBinding

class EsimsSettingsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_DEST = "dest"
        const val DEST_ESIMS = "esims"
        const val DEST_SETTINGS = "settings"
    }

    private lateinit var b: ActivityEsimsSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEsimsSettingsBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)

        val start = intent.getStringExtra(EXTRA_DEST) ?: DEST_ESIMS
        if (savedInstanceState == null) {
            if (start == DEST_SETTINGS) {
                b.bottomNav.selectedItemId = R.id.nav_settings
                showSettings()
            } else {
                b.bottomNav.selectedItemId = R.id.nav_esims
                showEsims()
            }
        }

        b.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> {
                    startActivity(
                        Intent(this, PackagesActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    )
                    finish()
                    true
                }
                R.id.nav_esims -> { showEsims(); true }
                R.id.nav_settings -> { showSettings(); true }
                else -> false
            }
        }

    }

    private fun showEsims() {
        supportActionBar?.title = getString(R.string.title_my_esims)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, EsimsFragment())
            .commit()
    }

    private fun showSettings() {
        supportActionBar?.title = getString(R.string.title_settings)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, SettingsFragment())
            .commit()
    }
}
