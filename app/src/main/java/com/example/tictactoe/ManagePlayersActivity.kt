package com.example.tictactoe

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityManagePlayersBinding

// Activity for managing players in the local game data file. Users can reset player stats,
// delete a specific player, or delete all players.
class ManagePlayersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagePlayersBinding
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityManagePlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonResetStats.setOnClickListener { resetStats() }
        binding.buttonDeletePlayer.setOnClickListener { showDeletePlayerFragment() }
        binding.buttonDeleteAllPlayers.setOnClickListener { showDeleteAllPlayersFragment() }
        binding.buttonManagePlayersBackToStats.setOnClickListener { finish() }
    }

    // Reset all player stats in the local game data file (asynchronous)
    private fun resetStats() {
        playerManager.asyncResetStats(this) { success ->
            if (success) {
                // Stats reset successfully
                appUtils.showToast(this, "Player stats have been reset")
            } else {
                // Error resetting stats
                appUtils.showToast(this, "Error resetting player stats")
            }
        }
    }

    // Show a dialog with dropdown menu of players to select from
    private fun showDeletePlayerFragment() {
        val deletePlayerDialog = DeletePlayerFragment()
        deletePlayerDialog.show(supportFragmentManager, DeletePlayerFragment.TAG)
    }

    // Show a dialog to confirm deletion of all players
    private fun showDeleteAllPlayersFragment() {
        val deleteAllPlayersDialog = DeleteAllPlayersFragment()
        deleteAllPlayersDialog.show(supportFragmentManager, DeleteAllPlayersFragment.TAG)
    }

}