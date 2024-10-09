package com.example.tictactoe

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityStandingsBinding

class StandingsActivity : AppCompatActivity() {

    // TODO Layout background isn't applying properly yet

    private lateinit var binding: ActivityStandingsBinding
    private lateinit var standingsList: ListView

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

        // Todo Create a method that gets the player data from the game data file and creates
        //  a list of Player objects to pass to the adapter for formatting
        val players = ArrayList<Player>()
        players.add(Player("Player 1", 5, 8, 62.5))
        players.add(Player("Player 2", 3, 6, 50.0))
        players.add(Player("Player 3", 2, 4, 50.0))

        val standingsListViewAdapter = StandingsListViewAdapter(this, players)
        standingsList.adapter = standingsListViewAdapter

        // Previous example
        //        val citiesList : ListView = view.findViewById(R.id.citiesListView)
        //        val cities : Array<String> = resources.getStringArray(R.array.cities)
        //        val adapter = CitiesAdapter(requireContext(), cities)
        //        citiesList.adapter = adapter
    }
}