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

        // TODO If no player names are entered to select from, show a dialog to enter them
        //  (player names list should be updated with the new names)

        // Asynchronously get the list of players from the local game data file
        playerManager.asyncGetPlayers(this) { players ->
            populateMenu(players)
            setupSpinnerListener()
        }

        binding.buttonSinglePlayerBackToSetup.setOnClickListener { finish() }
        binding.buttonPlayGameSingle.setOnClickListener { handlePlayGameVsAndroid() }
    }

    // Populate the spinner with the player names from the local game data file
    private fun populateMenu(players: List<Player>) {
        // Extract player names from the list
        val playerNames = players.map { it.name }.toMutableList()

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
        val intent = Intent(this, PlayGameActivity::class.java)
        startActivity(intent)
    }
}

// TODO NEXT: Handle entering a new player name on this screen (implement on multiple player activity, too)