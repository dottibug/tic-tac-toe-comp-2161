package com.example.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.tictactoe.databinding.FragmentDeletePlayerBinding

// Fragment for deleting a player from the local game data file
class DeletePlayerFragment : DialogFragment() {
    private var binding: FragmentDeletePlayerBinding? = null
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()
    private var playerToDelete : String = ""
    private var savedSpinnerPosition: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentDeletePlayerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            playerToDelete = savedInstanceState.getString("playerToDelete", "")
            savedSpinnerPosition = savedInstanceState.getInt("spinnerPosition", 0)
        }

        // Asynchronously get the list of players from the local game data file
        playerManager.asyncGetPlayers(requireContext()) { players ->
            populateMenu(players)
            setupSpinnerListener()
            // Restore the spinner position after populating
            if (playerToDelete.isNotEmpty()) {
                val position = (binding?.spinnerDeletePlayer?.adapter as? ArrayAdapter<String>)?.getPosition(playerToDelete) ?: -1
                if (position != -1) {
                    binding?.spinnerDeletePlayer?.setSelection(position)
                }
            } else {
                binding?.spinnerDeletePlayer?.setSelection(savedSpinnerPosition)
            }
        }

       binding?.buttonDeleteThisPlayer?.setOnClickListener { handleDeletePlayer() }
    }

    // Populate the spinner with the player names
    private fun populateMenu(players: List<Player>) {
        // Extract player names from the list
        val playerNames = players.map { it.name }.toMutableList()

        // Remove Android, Player One, and Player Two from the list
        playerNames.remove("Android")
        playerNames.remove("Player One")
        playerNames.remove("Player Two")

        // Populate the spinner with the player names
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, playerNames)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding?.spinnerDeletePlayer?.adapter = adapter
    }

    // Set up the spinner listener to get the selected player to delete
    private fun setupSpinnerListener() {
        binding?.spinnerDeletePlayer?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPlayer = parent?.getItemAtPosition(position) as String
                playerToDelete = selectedPlayer
                savedSpinnerPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                appUtils.showToast(requireContext(), "Select a player to delete")
            }
        }
    }

    // Delete the selected player from the local game data file
    private fun handleDeletePlayer() {
        // Delete the selected player from the local game data file
        playerManager.asyncDeletePlayer(requireContext(), playerToDelete) { success ->
            if (success) {
                appUtils.showToast(requireContext(), "$playerToDelete has been deleted")
            } else {
                appUtils.showToast(requireContext(), "Error deleting $playerToDelete")
            }
        }

        // Dismiss after a delay to give user time to read the toast message
        binding?.root?.postDelayed({ dismiss() }, 1000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = "DeletePlayerDialog"
    }

    // ----------------------------
    // DATA PERSISTENCE
    // ----------------------------
    // Save state of playerToDelete
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("playerToDelete", playerToDelete)
        outState.putInt("spinnerPosition", savedSpinnerPosition)
    }
}