package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonPlayerNames.setOnClickListener { onPlayerNamesClick() }
        binding.buttonNewGame.setOnClickListener { onNewGameClick() }
        binding.buttonStandings.setOnClickListener { onStandingsClick() }

    }

    private fun onPlayerNamesClick() {
        // Start the EnterNamesActivity
        val intent = Intent(this, EnterNamesActivity::class.java)
        startActivity(intent)
    }


    private fun onNewGameClick() {
        // Start the GameSetupActivity
        val intent = Intent(this, GameSetupActivity::class.java)
        startActivity(intent)
    }

    private fun onStandingsClick() {
        // Start the StandingsActivity
        val intent = Intent(this, StandingsActivity::class.java)
        startActivity(intent)
    }

}