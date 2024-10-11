package com.example.tictactoe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tictactoe.databinding.ActivityPlayGameBinding

class PlayGameActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityPlayGameBinding
    private lateinit var gameBoard: Array<Array<String>> // 3x3 game board represented as a 2D array of strings
    private lateinit var boardCells: Array<TextView>
    private lateinit var gameMode: String
    private lateinit var playerOne: String
    private lateinit var playerTwo: String
    private lateinit var currentPlayer: String
    private lateinit var currentToken: String

    // NOTE: Testing purposes for now
    private val appUtils = AppUtils()

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

        sharedPreferences = getSharedPreferences("GamePrefs", MODE_PRIVATE)

        binding.buttonGameGoToMainMenu.setOnClickListener { navigateToHome() }
        binding.buttonRestart.setOnClickListener { showRestartDialog() }

        boardCells = arrayOf(
            binding.textViewCell00, binding.textViewCell01, binding.textViewCell02,
            binding.textViewCell10, binding.textViewCell11, binding.textViewCell12,
            binding.textViewCell20, binding.textViewCell21, binding.textViewCell22
        )

        setupGame()
        setupCellListeners()
    }

    private fun setupGame() {
        setGameMode()
        setPlayers()
        setupFirstPlayer()
        setupGameBoard()
    }

    // Set game mode
    private fun setGameMode() {
        gameMode = sharedPreferences.getString("gameMode", "singlePlayer") ?: "singlePlayer"
    }

    private fun setPlayers() {
        if (gameMode == "singlePlayer") {
            playerOne = sharedPreferences.getString("selectedSinglePlayer", "Player One") ?: "Player One"
            playerTwo = "Android"
        } else {
            playerOne = sharedPreferences.getString("selectedPlayerOne", "Player One") ?: "Player One"
            playerTwo = sharedPreferences.getString("selectedPlayerTwo", "Player Two") ?: "Player Two"
        }
    }

    // Set the first player based on the game mode
    private fun setupFirstPlayer() {
        currentPlayer = playerOne
        currentToken = "X"
        binding.textViewPlayerTurn.text = String.format(getString(R.string.playerTurn), currentPlayer)
    }

    // Set up empty game board
    private fun setupGameBoard() {
        gameBoard = Array(3) { Array(3) { "" } }
    }

    // Set up button click listeners for each cell
    private fun setupCellListeners() {
        // When i is 0 to 2, i/3 = 0, indicating row 0
        // When i is 3 to 5, i/3 = 1, indicating row 1
        // When i is 6 to 8, i/3 = 2, indicating row 2

        // When i is 0, i%3 = 0, indicating column 0
        // When i is 1, i%3 = 1, indicating column 1
        // When i is 2, i%3 = 2, indicating column 2

        boardCells.forEachIndexed { index, cell ->
            val row = index / 3
            val col = index % 3

            cell.setOnClickListener {
                if (gameBoard[row][col].isEmpty()) {
                    placeMarker(row, col)
                }
            }
        }
    }

    private fun placeMarker(row: Int, col: Int) {
        // Update game board array with the appropriate token and update the UI
        gameBoard[row][col] = currentToken
        updateBoardUI()

        // Check for a win or draw
        if (gameWon()) { endGame("$currentPlayer won!") }
        else if (gameDraw()) { endGame("It's a draw!") }

        // Change current token and current player; initiate Android turn if single player mode
        // TODO: add delay on android placeMarker update
        else {
            changeToken()
            updateCurrentPlayer()
            if (gameMode == "singlePlayer" && currentToken == "O") {
                AsyncAndroidTurn(gameBoard) { move ->
                    runOnUiThread {
                        placeMarker(move.first, move.second)
                    }
                }.execute()
            }
        }

    }

    // Flatten game board to change the text views of each cell
    private fun updateBoardUI() {
        gameBoard.flatten().forEachIndexed { index, token ->
            boardCells[index].text = token
        }
    }

    private fun changeToken() {
        currentToken = if (currentToken == "X") "O" else "X"
    }

    private fun updateCurrentPlayer() {
        currentPlayer = if (currentPlayer == playerOne) { playerTwo } else { playerOne }
    }

    // game board array
    // | [ 00, 01, 02 ], |
    // | [ 10, 11, 12 ], |
    // | [ 20, 21, 22 ]  |

    // Check if the game is won
    private fun gameWon(): Boolean {
        // Check rows: If any row has all the same tokens, then the currentPlayer is the winner
        // TODO: change color of the winning line
        for (row in gameBoard) {
            if (row.all { it == "X" } || row.all { it == "O" }) { return true }
        }

        // Check columns: If any column has all the same tokens, then the currentPlayer is the winner
        // TODO: change color of the winning line
        for (col in 0..2) {
            if (gameBoard[0][col].isNotEmpty() && gameBoard[1][col].isNotEmpty() && gameBoard[2][col].isNotEmpty()) {
                if (gameBoard[0][col] == gameBoard[1][col] && gameBoard[1][col] == gameBoard[2][col]) {
                    return true
                }
            }

        }

        // Check diagonals: If any diagonal has all the same tokens, then the currentPlayer is the winner
        // TODO: change color of the winning line
        if (gameBoard[0][0].isNotEmpty() && gameBoard[1][1].isNotEmpty() && gameBoard[2][2].isNotEmpty()) {
            if (gameBoard[0][0] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][2]) { return true }
        }

        if (gameBoard[0][2].isNotEmpty() && gameBoard[1][1].isNotEmpty() && gameBoard[2][0].isNotEmpty()) {
            if (gameBoard[0][2] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][0]) { return true }
        }

        // No win condition is met
        return false
    }

    // Check if the game is drawn: If board is full but the game was not won, then it is a draw
    private fun gameDraw(): Boolean {
        return gameBoard.flatten().all { it.isNotEmpty() }
    }

    private fun endGame(message: String) {
        // NOTE: the toast is temporary for testing purposes
        appUtils.showToast(this, message)

        // TODO Change restart button to a play again button; If pressed, make sure to change the button back to "restart"

        // Disable clicking on the board cells
        // TODO Make sure to re-enable the board cells if the user presses "play again"
        boardCells.forEach { it.isEnabled = false }
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

// TODO: Make android turn logic more intelligent.
//      Instead of a random empty cell, have it block 2 Xs in a row/col/diagonal
//      or go between two Xs in the same row/col/diagonal
//      or place an O to win the game if it can.