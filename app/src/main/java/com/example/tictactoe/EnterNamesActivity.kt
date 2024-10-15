package com.example.tictactoe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityEnterNamesBinding

// Activity for entering player names. This activity is launched from the MainActivity.
// Players can enter their name to the local game data file.
// If the player already exists, the player will not be added.
class EnterNamesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnterNamesBinding
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEnterNamesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonAddPlayer.setOnClickListener { handleAddPlayerClick() }
        binding.buttonEnterNamesHome.setOnClickListener { finish() }
    }

    // Gets the player name from the input field and adds it to the local game data file if the
    // player does not already exist
    private fun handleAddPlayerClick() {
        val playerName = binding.editTextPlayerName.text.toString().trim()

        if (playerName.isNotEmpty()) {
            playerManager.asyncAddPlayer(this, playerName) { response ->

                // Player added successfully: toast message, clear input, return to the main activity
                if (response.success) {
                    appUtils.showToast(this, response.message)
                    binding.editTextPlayerName.text.clear()
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
        outState.putString("playerName", binding.editTextPlayerName.text.toString())
    }

    // Restore state of editTextPlayerName
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val playerName = savedInstanceState.getString("playerName", "")
        binding.editTextPlayerName.setText(playerName)
        binding.editTextPlayerName.setSelection(playerName.length)
    }
}