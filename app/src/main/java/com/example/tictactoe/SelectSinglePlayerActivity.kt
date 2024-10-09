package com.example.tictactoe

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivitySelectSinglePlayerBinding

class SelectSinglePlayerActivity : AppCompatActivity() {
   private lateinit var binding: ActivitySelectSinglePlayerBinding

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

        // NOTE If no player names are entered to select from, show a dialog to enter them
        //  (player names list should be updated with the new names)

        // Populate spinner
        // TODO - populate with names from game data file when implemented
        val spinner = binding.spinnerSinglePlayerName

        val adapter = ArrayAdapter(this, R.layout.spinner_item, resources.getStringArray(R.array.simpleTest))
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinner.adapter = adapter
    }
}