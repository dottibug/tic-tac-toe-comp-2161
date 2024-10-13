package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivitySelectSinglePlayerBinding

class SelectSinglePlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectSinglePlayerBinding
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySelectSinglePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Asynchronously get the list of players from the local game data file
        playerManager.asyncGetPlayers(this) { players ->
            populateMenu(players)
            setupSpinnerListener()
        }

        binding.buttonPlayGameSingle.setOnClickListener { handlePlayGameVsAndroid() }
        binding.buttonAddNewSinglePlayer.setOnClickListener { handleAddNewPlayer() }
        binding.buttonSinglePlayerBackToSetup.setOnClickListener { finish() }
    }

    // Populate the spinner with the player names from the local game data file
    private fun populateMenu(players: List<Player>) {
        // Extract player names from the list
        val playerNames = players.map { it.name }.toMutableList()

        // Remove "Android" from the list
        playerNames.remove("Android")

        // Remove generic "Player Two" from the list
        if ("Player Two" in playerNames) {
            playerNames.remove("Player Two")
        }

        // Populate the spinner with the player names
        val adapter = ArrayAdapter(this, R.layout.spinner_item, playerNames)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spinnerSinglePlayerName.adapter = adapter
    }

    private fun setupSpinnerListener() {
        binding.spinnerSinglePlayerName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPlayer = parent?.getItemAtPosition(position) as String
                saveSelectedPlayer(selectedPlayer)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                appUtils.showToast(this@SelectSinglePlayerActivity, "Select a player")
            }
        }
    }

    private fun saveSelectedPlayer(selectedPlayer: String) {
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedSinglePlayer", selectedPlayer)
        editor.apply()
    }

    private fun handlePlayGameVsAndroid() {
        // Start game play activity
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("gameMode", "singlePlayer")
        editor.apply()

        val intent = Intent(this, PlayGameActivity::class.java)
        startActivity(intent)
    }

    private fun handleAddNewPlayer() {
        val intent = Intent(this, GameSetupAddNewPlayerActivity::class.java)
        startActivity(intent)
    }

    // Update the player list when the user comes back from the GameSetupAddNewPlayerActivity
    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val newPlayerAdded = sharedPreferences.getBoolean("newPlayerAdded", false)

        if (newPlayerAdded) {
            updatePlayerList()
            sharedPreferences.edit().putBoolean("newPlayerAdded", false).apply()
        }
    }

    // Asynchronously get the list of players from the local game data file and update the player list
    // Select the newly added player by default
    private fun updatePlayerList() {
        playerManager.asyncGetPlayers(this) { players ->
            populateMenu(players)
            setupSpinnerListener()
            selectedNewlyAddedPlayer()
        }
    }

    // Select the newly added player by default in the spinner
    private fun selectedNewlyAddedPlayer() {
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val lastAddedPlayer = sharedPreferences.getString("lastAddedPlayer", null)

        if (lastAddedPlayer != null) {
            val position = (binding.spinnerSinglePlayerName.adapter as ArrayAdapter<String>).getPosition(lastAddedPlayer)
            if (position != -1) {
                binding.spinnerSinglePlayerName.setSelection(position)
            }
            // Clear the lastAddedPlayer from preferences
            sharedPreferences.edit().remove("lastAddedPlayer").apply()
        }
    }
}
