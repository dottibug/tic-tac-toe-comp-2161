package com.example.tictactoe

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

        // Asynchronously get the list of players from the local game data file
        playerManager.asyncGetPlayers(this) { players -> populateListView(players) }

        standingsList = binding.listViewStandings
        binding.buttonStandingsHome.setOnClickListener { finish() }
    }

    // Populate the standings list view with the players from the local game data file
    private fun populateListView(players: List<Player>) {
        val standingsListViewAdapter = StandingsListViewAdapter(this, players)
        standingsList.adapter = standingsListViewAdapter
    }

}