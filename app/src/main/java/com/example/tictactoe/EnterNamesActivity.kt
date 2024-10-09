package com.example.tictactoe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityEnterNamesBinding

class EnterNamesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEnterNamesBinding

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
    }
}

// Enter a player name
// Add the player name to local file (if it isn't already there)
// NOTE: Should writing/reading from the local file be a separate thread?
// If the player name is already in local file, toast that the player already exists
// The player names will be displayed when setting up a new game for players to select
    // from (include a link to add a new player on that screen)