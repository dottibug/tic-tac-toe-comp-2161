package com.example.tictactoe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class StandingsListViewAdapter(context: Context, data: ArrayList<Player>) : ArrayAdapter<Player>(context, 0, data) {

    // Override the getView() method to customize the layout for each list item
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate the custom layout for the list item
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false)
        val item = getItem(position)

        val playerName : TextView = itemView.findViewById(R.id.textViewListItemPlayer)
        // TODO set player name from the game data file

        val gamesWon : TextView = itemView.findViewById(R.id.textViewListItemWon)
        // TODO set games won from the game data file

        val gamesPlayed : TextView = itemView.findViewById(R.id.textViewListItemPlayed)
        // TODO set games played from the game data file

        val winPercentage : TextView = itemView.findViewById(R.id.textViewListItemWinPercentage)
        // TODO set win percentage from the game data file


        // Example
        // val cityName : TextView = itemView.findViewById(R.id.textViewListItem)
        // cityName.text = item
        return itemView
    }
}