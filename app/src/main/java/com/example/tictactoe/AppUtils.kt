package com.example.tictactoe

import android.content.Context
import android.widget.Toast

// Utility class for showing toast messages
class AppUtils () {
    var toast: Toast? = null

    // Show toast message (cancel any previous toasts first)
    fun showToast(context: Context, message: String) {
        toast?.cancel()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}