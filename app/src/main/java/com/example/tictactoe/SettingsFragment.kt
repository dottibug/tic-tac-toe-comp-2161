package com.example.tictactoe

import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        Log.i("testcat", "onCreatePreferences called")

        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setupNightModePreference()
        setupDifficultyPreference()
    }

    private fun setupNightModePreference() {
        Log.i("testcat", "night mode toggled")
        // TODO
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