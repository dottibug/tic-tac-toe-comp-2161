package com.example.tictactoe

import android.os.AsyncTask

// Asynchronous Android turn logic based on the settings difficulty level:
//      - Easy: Android picks a random empty cell to place an O
//      - Medium: Android places an O to win if it can, blocks 2 Xs in a row/col/diagonal,
//        or picks a random cell to place an O
//      - Hard: Android places an O to win if it can, blocks 2 Xs in a row/col/diagonal,
//        goes between 2 Xs in the same row/col/diagonal, or picks a random cell to place an O
class AsyncAndroidTurn(
    private val difficulty: String,
    private val gameBoard: Array<Array<String>>,
    private val callback: (Pair<Int, Int>) -> Unit
) : AsyncTask<Void, Void, Pair<Int, Int>>() {

        override fun doInBackground(vararg params: Void?): Pair<Int, Int>? {
            // Delay for 1 second to simulate "thinking" time
            Thread.sleep(1000)

            // Return the coordinates of Android's move based on the game difficulty setting
            return when (difficulty) {
                "Easy" -> chooseRandomEmptyCell()
                "Medium" -> placeToWin() ?: goBesideTwo("X") ?: chooseRandomEmptyCell()
                "Hard" -> placeToWin() ?: goBesideTwo("X") ?: goBetweenTwo("X") ?: chooseRandomEmptyCell()
                else -> chooseRandomEmptyCell() // Default to easy if invalid difficulty setting
            }
        }

        override fun onPostExecute(result: Pair<Int, Int>) {
            callback(result)
        }

    // Android picks a random empty cell to place an O
    private fun chooseRandomEmptyCell(): Pair<Int, Int>? {
        val emptyCells = mutableListOf<Pair<Int, Int>>()

        // Flatten the game board and check for empty cells
        gameBoard.flatten().forEachIndexed { index, token ->
            val row = index / 3
            val col = index % 3
            if (token.isEmpty()) { emptyCells.add(Pair(row, col)) }
        }

        // Return a random empty cell
        return emptyCells.randomOrNull()
    }

    // Game board 2D array
    // | [ 00, 01, 02 ], |
    // | [ 10, 11, 12 ], |
    // | [ 20, 21, 22 ]  |

    // Android checks rows, cols, and diagonals for a place to win
    private fun placeToWin(): Pair<Int, Int>? {
        return goBesideTwo("O") ?: goBetweenTwo("O")
    }

    // Android checks rows, cols, and diagonals for 2 consecutive tokens beside an empty cell (X-X-empty or empty-X-X)
    private fun goBesideTwo(token: String): Pair<Int, Int>? {
        // Check rows
        for (row in 0..2) {
            when {
                gameBoard[row][0] == token && gameBoard[row][1] == token && gameBoard[row][2].isEmpty() -> return Pair(row, 2)
                gameBoard[row][0].isEmpty() && gameBoard[row][1] == token && gameBoard[row][2] == token -> return Pair(row, 0)
            }
        }

        // Check columns
        for (col in 0..2) {
            when {
                gameBoard[0][col] == token && gameBoard[1][col] == token && gameBoard[2][col].isEmpty() -> return Pair(2, col)
                gameBoard[0][col].isEmpty() && gameBoard[1][col] == token && gameBoard[2][col] == token -> return Pair(0, col)
            }
        }

        // Check diagonals
        when {
            // Diagonal top left to bottom right
            gameBoard[0][0] == token && gameBoard[1][1] == token && gameBoard[2][2].isEmpty() -> return Pair(2, 2)
            gameBoard[0][0].isEmpty() && gameBoard[1][1] == token && gameBoard[2][2] == token -> return Pair(0, 0)

            // Diagonal top right to bottom left
            gameBoard[0][2] == token && gameBoard[1][1] == token && gameBoard[2][0].isEmpty() -> return Pair(2, 0)
            gameBoard[0][2].isEmpty() && gameBoard[1][1] == token && gameBoard[2][0] == token -> return Pair(0, 2)
        }

        return null
    }

    // Android checks rows, cols, and diagonals for an empty cell between 2 tokens (X-empty-X)
    private fun goBetweenTwo(token: String): Pair<Int, Int>? {
        // Check rows
        for (row in 0..2) {
            when {
                gameBoard[row][0] == token && gameBoard[row][1].isEmpty() && gameBoard[row][2] == token -> return Pair(row, 1)
            }
        }

        // Check columns
        for (col in 0..2) {
            when {
                gameBoard[0][col] == token && gameBoard[1][col].isEmpty() && gameBoard[2][col] == token -> return Pair(1, col)
            }
        }

        // Check diagonals
        when {
            // Diagonal top left to bottom right
            gameBoard[0][0] == token && gameBoard[1][1].isEmpty() && gameBoard[2][2] == token -> return Pair(1, 1)

            // Diagonal top right to bottom left
            gameBoard[0][2] == token && gameBoard[1][1].isEmpty() && gameBoard[2][0] == token -> return Pair(1, 1)
        }

        return null
    }
}

