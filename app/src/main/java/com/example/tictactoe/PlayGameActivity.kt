package com.example.tictactoe

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.preference.PreferenceManager
import com.example.tictactoe.databinding.ActivityPlayGameBinding

class PlayGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayGameBinding
    private lateinit var gameSettings: SharedPreferences
    private val playerManager = PlayerManager()
    private var androidTurnTask: AsyncAndroidTurn? = null
    private lateinit var gameBoard: Array<Array<String>> // 3x3 game board represented as a 2D array of strings
    private lateinit var defaultTextColorStateList: ColorStateList
    private lateinit var boardCells: Array<TextView>
    private lateinit var gameMode: String
    private lateinit var difficulty: String
    private lateinit var playerOne: String
    private lateinit var playerTwo: String
    private lateinit var currentPlayer: String
    private lateinit var currentToken: String
    private var winningCells: List<Int> = emptyList()
    private var gameEnded = false

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

        gameSettings = getSharedPreferences("GamePrefs", MODE_PRIVATE)

        binding.buttonGameGoToMainMenu.setOnClickListener { navigateToHome() }
        binding.buttonRestart.setOnClickListener { showRestartDialog() }

        boardCells = arrayOf(
            binding.textViewCell00, binding.textViewCell01, binding.textViewCell02,
            binding.textViewCell10, binding.textViewCell11, binding.textViewCell12,
            binding.textViewCell20, binding.textViewCell21, binding.textViewCell22
        )

        // Get the default text color state list of the board cells
        defaultTextColorStateList = boardCells[0].textColors

        if (savedInstanceState == null) {
            setupGame()
        } else {
            restoreGameState(savedInstanceState)
        }

//        setupGame()
        setupCellListeners()

        if (gameMode == "singlePlayer") {
            difficulty =
                PreferenceManager.getDefaultSharedPreferences(this).getString("difficulty", "Easy").toString()
        }
    }

    // Restore the game state from the saved instance state
    private fun restoreGameState(savedInstanceState: Bundle) {
        gameMode = savedInstanceState.getString("gameMode", "")
        difficulty = savedInstanceState.getString("difficulty", "Easy")
        playerOne = savedInstanceState.getString("playerOne", "Player One")
        playerTwo = savedInstanceState.getString("playerTwo", "Player Two")
        currentPlayer = savedInstanceState.getString("currentPlayer", "Player One")
        currentToken = savedInstanceState.getString("currentToken", "X")
        winningCells = savedInstanceState.getIntegerArrayList("winningCells") ?: emptyList()
        gameEnded = savedInstanceState.getBoolean("gameEnded", false)

        val flatBoard = savedInstanceState.getStringArrayList("gameBoard")
        gameBoard = Array(3) { row ->
            Array(3) { col ->
                flatBoard?.get(row * 3 + col) ?: ""
            }
        }

        updateBoardUI()

        // If the game has ended, restore the highlighted winning cells and other win conditions
        if (gameEnded) {
            binding.buttonRestart.text = getString(R.string.buttonPlayAgain)
            binding.buttonRestart.setOnClickListener { restartGame() }
            setBoardCellsEnabled(false)
            if (winningCells.isNotEmpty()) {
                highlightWinningCells()
                updateGameStatusText("$currentPlayer Won!")
            } else {
                updateGameStatusText("It's a Draw!")
            }
        } else {
            setBoardCellsEnabled(true)
            setPlayerTurn()
        }
    }

    // Set up the game
    private fun setupGame() {
        setGameMode()
        setPlayers()
        setupFirstPlayer()
        setupGameBoard()
        winningCells = emptyList()
    }

    // Set game mode
    private fun setGameMode() {
        gameMode = gameSettings.getString("gameMode", "").toString()
    }

    // Set player one and two for taking turns
    private fun setPlayers() {
        if (gameMode == "singlePlayer") {
            playerOne = gameSettings.getString("selectedSinglePlayer", "Player One") ?: "Player One"
            playerTwo = "Android"
        } else {
            playerOne = gameSettings.getString("selectedPlayerOne", "Player One") ?: "Player One"
            playerTwo = gameSettings.getString("selectedPlayerTwo", "Player Two") ?: "Player Two"
        }
    }

    // Set the first player based on the game mode
    private fun setupFirstPlayer() {
        currentPlayer = playerOne
        currentToken = "X"
        setPlayerTurn()
    }

    private fun setPlayerTurn() {
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
                if (gameBoard[row][col].isEmpty()) { placeMarker(row, col) }
            }
        }
    }

    private fun placeMarker(row: Int, col: Int) {
        // Update game board array with the appropriate token and update the UI
        gameBoard[row][col] = currentToken
        updateBoardUI()

        // Check for a win or draw
        if (gameWon()) {
            highlightWinningCells()
            endGame("$currentPlayer won!")
        }

        else if (gameDraw()) { endGame("It's a draw!") }

        // Change current token and current player; initiate Android turn if single player mode
        else {
            changeToken()
            updateCurrentPlayer()
            if (gameMode == "singlePlayer" && currentToken == "O") { startAndroidTurn() }
        }
    }

    // Flatten game board to change the text views of each cell
    private fun updateBoardUI() {
        gameBoard.flatten().forEachIndexed { index, token -> boardCells[index].text = token }
    }

    // Change current token to the opposite token
    private fun changeToken() { currentToken = if (currentToken == "X") "O" else "X" }

    // Change current player to the opposite player
    private fun updateCurrentPlayer() {
        currentPlayer = if (currentPlayer == playerOne) { playerTwo } else { playerOne }
        setPlayerTurn()
    }

    // Enable/disable clicking on the board cells
    private fun setBoardCellsEnabled(enabled: Boolean) {
        boardCells.forEach { it.isEnabled = enabled }
    }

    // Start Android turn (background task)
    private fun startAndroidTurn() {
        setBoardCellsEnabled(false)

        androidTurnTask = AsyncAndroidTurn(difficulty, gameBoard) { move ->
            runOnUiThread {
                setBoardCellsEnabled(true)
                placeMarker(move.first, move.second)
            }
        }
        androidTurnTask?.execute()
    }

    // game board array
    // | [ 00, 01, 02 ], |
    // | [ 10, 11, 12 ], |
    // | [ 20, 21, 22 ]  |

    // Check if the game is won
    // Sets the indices of the winning cells (0 to 8, to align with the boardCells array of textViews)
    private fun gameWon(): Boolean {
        // Check rows: If any row has all the same tokens, then the currentPlayer is the winner
        for (row in 0..2) {
            if (gameBoard[row].all { it == "X" } || gameBoard[row].all { it == "O" }) {
                winningCells = listOf(row*3, row*3+1, row*3+2)
                return true
            }
        }

        // Check columns: If any column has all the same tokens, then the currentPlayer is the winner
        for (col in 0..2) {
            if (gameBoard[0][col].isNotEmpty() && gameBoard[1][col].isNotEmpty() && gameBoard[2][col].isNotEmpty()) {
                if (gameBoard[0][col] == gameBoard[1][col] && gameBoard[1][col] == gameBoard[2][col]) {
                    winningCells = listOf(col, col+3, col+6)
                    return true
                }
            }
        }

        // Check diagonals: If any diagonal has all the same tokens, then the currentPlayer is the winner
        // Diagonal top left to bottom right
        if (gameBoard[0][0].isNotEmpty() && gameBoard[1][1].isNotEmpty() && gameBoard[2][2].isNotEmpty()) {
            if (gameBoard[0][0] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][2]) {
                winningCells = listOf(0, 4, 8)
                return true
            }
        }

        // Diagonal top right to bottom left
        if (gameBoard[0][2].isNotEmpty() && gameBoard[1][1].isNotEmpty() && gameBoard[2][0].isNotEmpty()) {
            if (gameBoard[0][2] == gameBoard[1][1] && gameBoard[1][1] == gameBoard[2][0]) {
                winningCells = listOf(2, 4, 6)
                return true
            }
        }

        // No win condition is met
        return false
    }

    // Highlight the winning cells in the UI
    private fun highlightWinningCells() {
        for (cellIndex in winningCells) {
            val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
            val colorId = if (isDarkMode) R.color.mattePumpkin else R.color.pumpkin
            boardCells[cellIndex].setTextColor(ContextCompat.getColor(this, colorId))
        }
    }

    // Check if the game is drawn: If board is full but the game was not won, then it is a draw
    private fun gameDraw(): Boolean {
        return gameBoard.flatten().all { it.isNotEmpty() }
    }

    // End the game and update the player stats in the local game data file
    private fun endGame(message: String) {
        when {
            message.contains("won") -> {
                playerManager.asyncUpdatePlayerStats(this, currentPlayer, "win")
                playerManager.asyncUpdatePlayerStats(this, if (currentPlayer == playerOne) playerTwo else playerOne, "loss")
                highlightWinningCells()
                updateGameStatusText("$currentPlayer Won!")
            }

            message.contains("draw") -> {
                playerManager.asyncUpdatePlayerStats(this, playerOne, "tie")
                playerManager.asyncUpdatePlayerStats(this, playerTwo, "tie")
                updateGameStatusText("It's a Draw!")
            }
        }

        // NOTE: the toast is temporary for testing purposes
        appUtils.showToast(this, message)

        gameEnded = true
        binding.buttonRestart.text = getString(R.string.buttonPlayAgain)
        binding.buttonRestart.setOnClickListener { restartGame() }

        // Disable clicking on the board cells
        setBoardCellsEnabled(false)
    }

    private fun restartGame() {
        gameEnded = false
        binding.buttonRestart.text = getString(R.string.buttonRestart)
        binding.buttonRestart.setOnClickListener { showRestartDialog() }

        setupGame()
        setupFirstPlayer()
        updateBoardUI()
        setBoardCellsEnabled(true)
        setPlayerTurn()
        winningCells = emptyList()

        // Reset cell colors to the initial color
        boardCells.forEach { cell ->
            cell.setTextColor(defaultTextColorStateList)
        }
    }

    // Update the game status text
    private fun updateGameStatusText(message: String) {
        binding.textViewPlayerTurn.text = message
    }

    // Show the restart dialog
    private fun showRestartDialog() {
        val restartDialog = RestartDialogFragment {
            restartGame()
        }
        restartDialog.show(supportFragmentManager, RestartDialogFragment.TAG)
    }

    // Navigate to the home screen
    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    // Cancel background task if the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        androidTurnTask?.cancel(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("gameMode", gameMode)
        outState.putString("difficulty", difficulty)
        outState.putString("playerOne", playerOne)
        outState.putString("playerTwo", playerTwo)
        outState.putString("currentPlayer", currentPlayer)
        outState.putString("currentToken", currentToken)
        outState.putBoolean("gameEnded", gameEnded)
        outState.putStringArrayList("gameBoard", ArrayList(gameBoard.flatten()))
        outState.putIntegerArrayList("winningCells", ArrayList(winningCells))
    }
}
