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

// Activity for selecting a single player for single player game play.
class SelectSinglePlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectSinglePlayerBinding
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()
    private var savedSpinnerPosition: Int = 0

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

        // Restore state of spinner selection, if applicable
        if (savedInstanceState != null) {
            savedSpinnerPosition = savedInstanceState.getInt("spinnerSelection", 0)
        }

        // Asynchronously get the list of players from the local game data file
        playerManager.asyncGetPlayers(this) { players ->
            populateMenu(players)
            setupSpinnerListener()
            binding.spinnerSinglePlayerName.setSelection(savedSpinnerPosition)
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

    // Set up the listener for the spinner
    private fun setupSpinnerListener() {
        binding.spinnerSinglePlayerName.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPlayer = parent?.getItemAtPosition(position) as String
                saveSelectedPlayer(selectedPlayer)
                savedSpinnerPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                appUtils.showToast(this@SelectSinglePlayerActivity, "Select a player")
            }
        }
    }

    // Save the selected player to shared preferences (NOTE: not the root preferences from settings)
    private fun saveSelectedPlayer(selectedPlayer: String) {
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selectedSinglePlayer", selectedPlayer)
        editor.apply()
    }

    // Handle the click event for the "Play Game" button
    private fun handlePlayGameVsAndroid() {
        // Start game play activity
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("gameMode", "singlePlayer")
        editor.apply()

        val intent = Intent(this, PlayGameActivity::class.java)
        startActivity(intent)
    }

    // Handle the click event for the "Add New Player" button
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

    // ----------------------------
    // DATA PERSISTENCE
    // ----------------------------
    // Save state of spinner selection
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("spinnerSelection", savedSpinnerPosition)
    }
}
