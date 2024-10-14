package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import com.example.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val playerManager = PlayerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPlayerDataFile()
        setupDarkModePreference()

        // Set up the buttons
        binding.buttonPlayerNames.setOnClickListener { onPlayerNamesClick() }
        binding.buttonNewGame.setOnClickListener { onNewGameClick() }
        binding.buttonStandings.setOnClickListener { onStandingsClick() }
        binding.buttonSettings.setOnClickListener { onSettingsClick() }
    }

    // Set up the local game data file
    private fun setupPlayerDataFile() {
        // NOTE: Dev purposes only (to delete local file when testing)
//         playerManager.deletePlayerDataFile(this)

        playerManager.createPlayerDataFile(this)
    }

    // Start the EnterNamesActivity to add player names to the local game data file
    private fun onPlayerNamesClick() {
        val intent = Intent(this, EnterNamesActivity::class.java)
        startActivity(intent)
    }

    // Start the GameSetupActivity to set up a new game
    private fun onNewGameClick() {
        val intent = Intent(this, GameSetupActivity::class.java)
        startActivity(intent)
    }

    // Start the StandingsActivity to view player standings from the local game data file
    private fun onStandingsClick() {
        val intent = Intent(this, StandingsActivity::class.java)
        startActivity(intent)
    }

    // Start the SettingsActivity to change game settings
    private fun onSettingsClick() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun setupDarkModePreference() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("darkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}