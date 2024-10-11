package com.example.tictactoe

import android.os.AsyncTask

class AsyncAndroidTurn(private val gameBoard: Array<Array<String>>,
    private val callback: (Pair<Int, Int>) -> Unit) : AsyncTask<Void, Void, Pair<Int, Int>>() {

        override fun doInBackground(vararg params: Void?): Pair<Int, Int>? {
            // Array of empty cells to randomly choose from
            val emptyCells = mutableListOf<Pair<Int, Int>>()

            gameBoard.flatten().forEachIndexed { index, token ->
                val row = index / 3
                val col = index % 3
                if (token.isEmpty()) { emptyCells.add(Pair(row, col)) }
            }

            return emptyCells.randomOrNull()
        }

        override fun onPostExecute(result: Pair<Int, Int>) {
            callback(result)
        }
}
