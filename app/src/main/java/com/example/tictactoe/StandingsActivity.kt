package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityStandingsBinding

// StandingsActivity class for displaying a list of players stats.
// Stats include player name, games won, games played, and win percentage
// Player data is retrieved asynchronously from the local game data file using PlayerManager
class StandingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStandingsBinding
    private lateinit var standingsList: ListView
    private val playerManager = PlayerManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityStandingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        standingsList = binding.listViewStandings
        binding.buttonStandingsHome.setOnClickListener { finish() }
        binding.buttonManagePlayers.setOnClickListener { handleManagePlayersClick() }

        // Asynchronously get the list of players from the local game data file
        refreshStandings()
    }

    // Populate the standings list view with the players from the local game data file
    private fun populateListView(players: List<Player>) {
        // Remove "Player One" and "Player Two" from the list
        val filteredPlayers = players.filter { it.name != "Player One" && it.name != "Player Two" }

        // Sort the players by name in alphabetical order
        val orderedPlayers = filteredPlayers.sortedBy { it.name }

        val standingsListViewAdapter = StandingsListViewAdapter(this, orderedPlayers)
        standingsList.adapter = standingsListViewAdapter
    }

    // Handle the click event for the "Manage Players" button
    private fun handleManagePlayersClick() {
        val intent = Intent(this, ManagePlayersActivity::class.java)
        startActivity(intent)
    }

    // Refresh the standings list view when the activity is resumed
    override fun onResume() {
        super.onResume()
        refreshStandings()
    }

    // Refresh the standings list view by asynchronously getting the players from the local game data file
    private fun refreshStandings() {
        playerManager.asyncGetPlayers(this) { players -> populateListView(players) }
    }
}