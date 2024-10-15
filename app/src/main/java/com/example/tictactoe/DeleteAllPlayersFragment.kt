package com.example.tictactoe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.tictactoe.databinding.FragmentDeleteAllPlayersBinding

// Fragment for deleting a player from the local game data file
class DeleteAllPlayersFragment : DialogFragment() {

    private var binding: FragmentDeleteAllPlayersBinding? = null
    private val playerManager = PlayerManager()
    private val appUtils = AppUtils()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentDeleteAllPlayersBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.buttonConfirmDeletePlayers?.setOnClickListener { handleDeletePlayers() }
    }

    // Delete all players from the local game data file (asynchronous)
    private fun handleDeletePlayers() {
       playerManager.asyncDeleteAllPlayers(requireContext()) { success ->
           if (success) {
               appUtils.showToast(requireContext(), "All players have been deleted")
           } else {
               appUtils.showToast(requireContext(), "Error deleting all players")
           }
       }

        // Dismiss after a delay to give user time to read the toast message
        binding?.root?.postDelayed({ dismiss() }, 1000)
    }

    // Clean up binding when the view is destroyed to avoid memory leaks
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    // Tag for the fragment
    companion object {
        const val TAG = "DeleteAllPlayersDialog"
    }
}