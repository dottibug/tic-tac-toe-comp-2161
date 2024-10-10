package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityGameSetupBinding

// GameSetupActivity class for setting up a new game. Selecting a game mode (single player
// or multi-player) sends user to the appropriate activity.
class GameSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonGameVsAndroid.setOnClickListener { onGameVsAndroidClick() }
        binding.buttonGameVsPlayer.setOnClickListener { onGameVsPlayerClick() }
        binding.buttonGameSetupHome.setOnClickListener { finish() }
    }

    // Start the SelectSinglePlayerActivity to select a single player
    private fun onGameVsAndroidClick() {
        val intent = Intent(this, SelectSinglePlayerActivity::class.java)
        startActivity(intent)
    }

    // Start the SelectMultiPlayerActivity to select two players
    private fun onGameVsPlayerClick() {
        val intent = Intent(this, SelectMultiPlayerActivity::class.java)
        startActivity(intent)
    }
}