package com.example.tictactoe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.tictactoe.databinding.FragmentRestartDialogBinding

class RestartDialogFragment : DialogFragment() {
    // Null until fragment is created (releases binding on destroy to save memory)
    private var binding: FragmentRestartDialogBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentRestartDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.buttonRestartGame?.setOnClickListener {
            // TODO: Restart game logic here
            Log.i("testcat", "Restart game button clicked; implement logic")
            dismiss()
        }

        binding?.buttonContinueGame?.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = "RestartDialog"
    }
}