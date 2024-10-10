package com.example.tictactoe

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import java.io.File

// Player data class
data class Player(val name: String, val gamesWon: Int, val gamesPlayed: Int, val winPercentage: Double)

// PlayerManager class for managing player data in the local game data file and creating Player objects
// Reading and writing to the local game data file is done asynchronously using AsyncTask
class PlayerManager() {
    private val PLAYERDATA = "playerData.txt"

    data class AddPlayerResponse(val success: Boolean, val message: String)

    // Create the local game data file if it does not already exist
    // Generic Player One and Player Two are added to the file (but will not be used for standings)
    fun createPlayerDataFile(context: Context) {
        val playerDataFile = getPlayerData(context)

        if (!playerDataFile.exists()) {
            playerDataFile.createNewFile()
            playerDataFile.appendText("Player One,0,0,0.0\n")
            playerDataFile.appendText("Player Two,0,0,0.0\n")
        }

        Log.i("testcat", "player data file created")
        Log.i("testcat", playerDataFile.readLines().toString())
    }

    fun deletePlayerDataFile(context: Context) {
        val playerDataFile = getPlayerData(context)
        playerDataFile.delete()
    }

    // Access the local game data file
    fun getPlayerData(context: Context): File {
        return File(context.filesDir, PLAYERDATA)
    }

    // Get player names from the local game data file
    fun asyncGetPlayers(context: Context, callback: (players: List<Player>) -> Unit) {
        val file = getPlayerData(context)
        GetPlayersTask(file, callback).execute()
    }

    // Add a player to the local game data file
    fun asyncAddPlayer(context: Context, playerName: String, callback: (response: AddPlayerResponse) -> Unit) {
        val file = getPlayerData(context)
        AddPlayerTask(file, playerName, callback).execute()
    }

    // Update player data in the local game data file
    fun updatePlayer() {}

    // Delete player data from the local game data file
    fun deletePlayer() {}

    // Creates a player object
   fun createPlayer(name: String, gamesWon: Int, gamesPlayed: Int, winPercentage: Double): Player {
       return Player(name, gamesWon, gamesPlayed, winPercentage)
   }


    // ------------------------------------------------
    // ASYNC TASK SUBCLASSES
    // ------------------------------------------------

    // Add a player to the local game data file if the player does not already exist
    // Returns a response object with a success boolean and message string
    private class AddPlayerTask(private val file: File, private val playerName: String,
        private val callback: (response: AddPlayerResponse) -> Unit): AsyncTask<Void, Void, AddPlayerResponse>() {

            override fun doInBackground(vararg params: Void?): AddPlayerResponse {
                val playerFileSet = file.readText()

                // Player already exists
                if (playerName in playerFileSet) { return AddPlayerResponse(false, "Player already exists") }

                // Add new player to file
                file.appendText("$playerName,0,0,0.0\n")
                return AddPlayerResponse(true, "Player added successfully")
            }

            override fun onPostExecute(result: AddPlayerResponse) {
                callback(result)
            }
    }

    // Get all player names from the local game data file
    // Returns a list of Player objects
    private class GetPlayersTask(private val file: File,
        private val callback: (players: List<Player>) -> Unit): AsyncTask<Void, Void, List<Player>>() {

            override fun doInBackground(vararg params: Void?): List<Player> {
                val playerFileList = file.readLines().toMutableList() as List<String>

                // Convert the player file list to a list of Player objects
                val players = playerFileList.map { line ->
                    val parts = line.split(",")
                    Player(parts[0], parts[1].toInt(), parts[2].toInt(), parts[3].toDouble())
                }

                return players
            }

            override fun onPostExecute(result: List<Player>) {
                callback(result)
            }
    }

}