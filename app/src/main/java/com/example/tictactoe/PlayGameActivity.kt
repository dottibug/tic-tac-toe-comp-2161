package com.example.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityPlayGameBinding

class PlayGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPlayGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonGameGoToMainMenu.setOnClickListener { navigateToHome() }
        binding.buttonRestart.setOnClickListener { showRestartDialog() }
    }

    // Show the restart dialog
    private fun showRestartDialog() {
        val restartDialog = RestartDialogFragment()
        restartDialog.show(supportFragmentManager, RestartDialogFragment.TAG)
    }

    // Navigate to the home screen
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}