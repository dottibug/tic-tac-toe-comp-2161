package com.example.tictactoe

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setupDarkModePreference()
        setupDifficultyPreference()
    }

    private fun setupDarkModePreference() {
        val darkModePreference = findPreference<SwitchPreferenceCompat>("darkMode")

        // Set the initial state of the switch based on the current night mode
        darkModePreference?.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        // Set up the listener for the switch
        darkModePreference?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkMode = newValue as Boolean
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Save the new state of the switch in SharedPreferences
            PreferenceManager.getDefaultSharedPreferences(requireContext())
                .edit()
                .putBoolean("darkMode", isDarkMode)
                .apply()

            true
        }
    }

    private fun setupDifficultyPreference() {
        val difficultyPreference = findPreference<ListPreference>("difficulty")

        difficultyPreference?.setOnPreferenceChangeListener { _, newValue ->
            Log.i("testcat", "difficulty changed to $newValue")
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
            sharedPreferences.edit().putString("difficulty", newValue as String).apply()
            true
        }

        Log.i("testcat", "Current difficulty: ${difficultyPreference?.value}")
    }
}

// TODO: Settings: Difficulty vs Android, dark mode, delete player, reset scores, clear all player data