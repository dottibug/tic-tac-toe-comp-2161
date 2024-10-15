package com.example.tictactoe

import android.content.Context
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.os.AsyncTask
import java.io.File
import java.util.Date
import java.util.Locale

// Player data class
data class Player(val name: String, val totalGames: Int, val losses: Int, val ties: Int,
    val wins: Int, val winPercentage: String, val lastPlayed: String)

// PlayerManager class for managing player data in the local game data file and creating Player objects
// Reading and writing to the local game data file is done asynchronously using AsyncTask
// A synchronization lock is used to ensure only one thread can access the local file at a time (to
// avoid outdated data being read or written to the file)
class PlayerManager() {
    private val PLAYERDATA = "playerData.txt"
    private val fileLock = Any()

    data class AddPlayerResponse(val success: Boolean, val message: String)

    // Create the local game data file if it does not already exist
    // Generic Player One and Player Two are added to the file (but will not be used for standings)
    fun createPlayerDataFile(context: Context) {
        val playerDataFile = getPlayerData(context)

        if (!playerDataFile.exists()) {
            playerDataFile.createNewFile()
            playerDataFile.appendText("Android,0,0,0,0,0%,---\n")
            playerDataFile.appendText("Player One,0,0,0,0,0%,---\n")
            playerDataFile.appendText("Player Two,0,0,0,0,0%,---\n")
        }
    }

    // Access the local game data file
    private fun getPlayerData(context: Context): File {
        return File(context.filesDir, PLAYERDATA)
    }

    // Get player names from the local game data file
    fun asyncGetPlayers(context: Context, callback: (players: List<Player>) -> Unit) {
        val file = getPlayerData(context)
        GetPlayersTask(fileLock, file, callback).execute()
    }

    // Add a player to the local game data file
    fun asyncAddPlayer(context: Context, playerName: String, callback: (response: AddPlayerResponse) -> Unit) {
        val file = getPlayerData(context)
        AddPlayerTask(fileLock, file, playerName, callback).execute()
    }

    // Update player data in the local game data file
    fun asyncUpdatePlayerStats(context: Context, playerName: String, event: String) {
        val file = getPlayerData(context)
        UpdatePlayerStatsTask(fileLock, file, playerName, event).execute()
    }

    fun asyncResetStats(context: Context, callback: (Boolean) -> Unit) {
        val file = getPlayerData(context)
        ResetStatsTask(fileLock, file, callback).execute()
    }

    // Delete player data from the local game data file
    fun asyncDeletePlayer(context: Context, playerName: String, callback: (Boolean) -> Unit) {
        val file = getPlayerData(context)
        DeletePlayerTask(fileLock, file, playerName, callback).execute()
    }

    fun asyncDeleteAllPlayers(context: Context, callback: (Boolean) -> Unit) {
        val file = getPlayerData(context)
        DeleteAllPlayersTask(fileLock, file, callback).execute()
    }

    // NOTE: For development purposes only
    fun deletePlayerDataFile(context: Context) {
        val file = getPlayerData(context)
        file.delete()
    }

    // ------------------------------------------------
    // ASYNC TASK SUBCLASSES
    // ------------------------------------------------

    // Add a player to the local game data file if the player does not already exist
    // Returns a response object with a success boolean and message string
    private class AddPlayerTask(
        private val fileLock: Any,
        private val file: File,
        private val playerName: String,
        private val callback: (response: AddPlayerResponse) -> Unit
    ) : AsyncTask<Void, Void, AddPlayerResponse>() {

        override fun doInBackground(vararg params: Void?): AddPlayerResponse {
            synchronized(fileLock) {
                val players = file.readLines().toMutableList()

                // Check if player already exists
                if (players.any { it.startsWith("$playerName,") }) {
                    return AddPlayerResponse(false, "Player already exists")
                }

                // Add new player to file
                players.add("$playerName,0,0,0,0,0%,---")
                file.writeText(players.joinToString("\n"))
                return AddPlayerResponse(true, "Player added successfully")
            }
        }

        override fun onPostExecute(result: AddPlayerResponse) {
            callback(result)
        }
    }

    // Get all player names from the local game data file
    // Returns a list of Player objects
    private class GetPlayersTask(
        private val fileLock: Any,
        private val file: File,
        private val callback: (players: List<Player>) -> Unit
    ) : AsyncTask<Void, Void, List<Player>>() {

        override fun doInBackground(vararg params: Void?): List<Player> {
            synchronized(fileLock) {
                val playerFileList = file.readLines().toMutableList() as List<String>

                // Convert the player file list to a list of Player objects
                val players = playerFileList.map { line ->
                    val parts = line.split(",")
                    Player(
                        parts[0],  // name
                        parts[1].toInt(),  // totalGames
                        parts[2].toInt(),  // losses
                        parts[3].toInt(),  // ties
                        parts[4].toInt(),  // wins
                        parts[5],  // winPercentage
                        parts[6]  // lastPlayed
                    )
                }
                return players
            }
        }

        override fun onPostExecute(result: List<Player>) {
            callback(result)
        }
    }

    // Update player stats in the local game data file
    private class UpdatePlayerStatsTask(
        private val fileLock: Any,
        private val file: File,
        private val playerName: String,
        private val event: String
    ) : AsyncTask<Void, Void, Unit>() {

        override fun doInBackground(vararg params: Void?) {
            synchronized(fileLock) {
                val players = file.readLines().toMutableList()
                val playerIndex = players.indexOfFirst { it.startsWith("$playerName,") }

                if (playerIndex != -1) {
                    val parts = players[playerIndex].split(",")
                    val totalGames = parts[1].toInt() + 1
                    val losses = parts[2].toInt() + if (event == "loss") 1 else 0
                    val ties = parts[3].toInt() + if (event == "tie") 1 else 0
                    val wins = parts[4].toInt() + if (event == "win") 1 else 0
                    val winPercentage = getWinPercentage(wins, totalGames)
                    val lastPlayed = getDate()

                    players[playerIndex] =
                        "$playerName,$totalGames,$losses,$ties,$wins,${winPercentage},$lastPlayed"

                    file.writeText(players.joinToString("\n"))
                }
            }
        }

        // Calculate and format the win percentage stat
        private fun getWinPercentage(wins: Int, totalGames: Int): String {
            val percentFormat = DecimalFormat("#.##'%'")
            val winPercentage = (wins.toDouble() / totalGames.toDouble()) * 100
            return percentFormat.format(winPercentage)
        }

        // Get and format the current date. Date format: Jan 1 2024 12:30 AM
        private fun getDate(): String {
            val milliseconds = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("MMM d 'at' h:mm a", Locale.CANADA)
            val formattedDate = dateFormat.format(Date(milliseconds))
            return formattedDate
        }
    }

    // Reset all player stats in the local game data file
    private class ResetStatsTask(
        private val fileLock: Any,
        private val file: File,
        private val callback: (Boolean) -> Unit
    ): AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            synchronized(fileLock) {
                val players = file.readLines().map { line ->
                    val name = line.split(",")[0]
                    "$name,0,0,0,0,0%,---"
                }
                file.writeText(players.joinToString("\n"))
                return true
            }
        }

        override fun onPostExecute(result: Boolean) {
            callback(result)
        }
    }

    // Delete a player from the local game data file
    private class DeletePlayerTask(
        private val fileLock: Any,
        private val file: File,
        private val playerName: String,
        private val callback: (Boolean) -> Unit
    ) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void?): Boolean {
            synchronized(fileLock) {
                // Filter out the player to delete from the list
                val players = file.readLines().filter { !it.startsWith("$playerName,") }
                file.writeText(players.joinToString("\n"))
                return true
            }
        }

        override fun onPostExecute(result: Boolean) {
            callback(result)
        }
    }

    // Delete all players from the local game data file
    private class DeleteAllPlayersTask(
        private val fileLock: Any,
        private val file: File,
        private val callback: (Boolean) -> Unit
    ): AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            synchronized(fileLock) {
                val playersToKeep = listOf("Android", "Player One", "Player Two")

                // Filter out all players except Android, Player One, and Player Two from the list
                val players = file.readLines().filter { line ->
                    playersToKeep.any { player -> line.startsWith("$player,") }
                }
                file.writeText(players.joinToString("\n"))
                return true
            }
        }

        override fun onPostExecute(result: Boolean) {
            callback(result)
        }
    }
}
