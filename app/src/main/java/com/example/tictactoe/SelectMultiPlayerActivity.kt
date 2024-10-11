package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivitySelectMultiPlayerBinding

class SelectMultiPlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectMultiPlayerBinding
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()
    private lateinit var spinnerPlayerOne: Spinner
    private lateinit var spinnerPlayerTwo: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySelectMultiPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        spinnerPlayerOne = binding.spinnerPlayerOneName
        spinnerPlayerTwo = binding.spinnerPlayerTwoName

        // Asynchronously get the list of players from the local game data file
        playerManager.asyncGetPlayers(this) { players ->
            populateMenus(players)
            setupSpinnerListener(spinnerPlayerOne, "PlayerOne")
            setupSpinnerListener(spinnerPlayerTwo, "PlayerTwo")
        }

        binding.buttonMultiPlayerBackToSetup.setOnClickListener { finish() }
        binding.buttonAddNewMultiPlayer.setOnClickListener { handleAddNewPlayer() }
        binding.buttonPlayGameMultiplayer.setOnClickListener { handlePlayGameVsPlayers() }
    }

    // Populate the spinners with the player names from the local game data file
    private fun populateMenus(players: List<Player>) {
        // Extract player names from the list
        val playerOneNames = players.map { it.name }.toMutableList().apply { remove("Player Two") }
        val playerTwoNames = players.map { it.name }.toMutableList().apply { remove("Player One") }

        // Populate the spinners with the player names
        setupSpinnerAdapter(playerOneNames, spinnerPlayerOne)
        setupSpinnerAdapter(playerTwoNames, spinnerPlayerTwo)
    }

    // Set up the adapter for each spinner
    private fun setupSpinnerAdapter(players: MutableList<String>, spinner: Spinner) {
        val adapter = ArrayAdapter(this, R.layout.spinner_item, players)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // Set up the listener for each spinner
    private fun setupSpinnerListener(spinner: Spinner, playerNumber: String) {
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPlayer = parent?.getItemAtPosition(position) as String
                saveSelectedPlayer(selectedPlayer, playerNumber)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                appUtils.showToast(this@SelectMultiPlayerActivity, "Select $playerNumber")
            }
        }
    }

    // Save the selected players to shared preferences as selectedPlayerOne and selectedPlayerTwo
    private fun saveSelectedPlayer(selectedPlayer: String, playerNumber: String) {
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("selected$playerNumber", selectedPlayer)
        editor.apply()
    }

    private fun handlePlayGameVsPlayers() {
        // Get the selected player names from shared preferences
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)

        val selectedPlayerOne = sharedPreferences.getString("selectedPlayerOne", null)
        val selectedPlayerTwo = sharedPreferences.getString("selectedPlayerTwo", null)

        // Check that the players are not the same
        if (selectedPlayerOne == selectedPlayerTwo) {
            // Show a toast message and do not start the game if the players are the same
            appUtils.showToast(this, "Select two different players")
            return
        } else {
            // Start game play activity if the players are different
            val editor = sharedPreferences.edit()
            editor.putString("gameMode", "multiPlayer")
            editor.apply()

            val intent = Intent(this, PlayGameActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleAddNewPlayer() {
        val intent = Intent(this, GameSetupAddNewPlayerActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)
        val newPlayerAdded = sharedPreferences.getBoolean("newPlayerAdded", false)

        if (newPlayerAdded) {
            updatePlayerList()
            sharedPreferences.edit().putBoolean("newPlayerAdded", false).apply()
        }
    }

    private fun updatePlayerList() {
        playerManager.asyncGetPlayers(this) { players ->
            populateMenus(players)
            setupSpinnerListener(spinnerPlayerOne, "PlayerOne")
            setupSpinnerListener(spinnerPlayerTwo, "PlayerTwo")
        }
    }
}