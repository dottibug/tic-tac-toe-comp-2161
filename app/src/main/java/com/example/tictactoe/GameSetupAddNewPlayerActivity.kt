package com.example.tictactoe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityGameSetupAddNewPlayerBinding

class GameSetupAddNewPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameSetupAddNewPlayerBinding
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameSetupAddNewPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonAddPlayerGameSetup.setOnClickListener { handleAddPlayerClick() }
        binding.buttonAddPlayerBackToSetup.setOnClickListener { finish() }
    }

    private fun handleAddPlayerClick() {
        val playerName = binding.editTextAddPlayerName.text.toString().trim()

        if (playerName.isNotEmpty()) {
            playerManager.asyncAddPlayer(this, playerName) { response ->

                // Player added successfully: toast message, clear input, return to the main activity
                if (response.success) {
                    appUtils.showToast(this, response.message)
                    binding.editTextAddPlayerName.text.clear()

                    // Save the newly added player to shared preferences to be used as the
                    // selected player in the game setup spinner
                    val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("lastAddedPlayer", playerName)
                    editor.putBoolean("newPlayerAdded", true)
                    editor.apply()

                    finish()
                }

                // Player already exists: toast message
                else { appUtils.showToast(this, response.message) }
            }
        }
    }

    // ----------------------------
    // DATA PERSISTENCE
    // ----------------------------
    // Save state of editTextPlayerName
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("playerName", binding.editTextAddPlayerName.text.toString())
    }

    // Restore state of editTextPlayerName
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.editTextAddPlayerName.setText(savedInstanceState.getString("playerName", ""))
    }
}