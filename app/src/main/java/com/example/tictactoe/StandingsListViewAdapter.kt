package com.example.tictactoe

import android.content.Context
import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// StandingsListViewAdapter class for customizing the layout of each list item in the standings list view
class StandingsListViewAdapter(context: Context, playerList: List<Player>) : ArrayAdapter<Player>(context, 0, playerList) {

    private val percentFormat = DecimalFormat("#.##'%'")

    // Override the getView() method to customize the layout for each list item
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate the custom layout for the list item
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false)
        val item = getItem(position)

        val playerName: TextView = itemView.findViewById(R.id.textViewListItemPlayer)
        playerName.text = item?.name

        val gamesWon: TextView = itemView.findViewById(R.id.textViewListItemWon)
        gamesWon.text = item?.gamesWon.toString()

        val gamesPlayed: TextView = itemView.findViewById(R.id.textViewListItemPlayed)
        gamesPlayed.text = item?.gamesPlayed.toString()

        val winPercentage: TextView = itemView.findViewById(R.id.textViewListItemWinPercentage)
        winPercentage.text = formatPercentage(item?.winPercentage ?: 0.0)

        return itemView
    }

    // Format the win percentage to display as a percentage with up to two decimal places
    private fun formatPercentage(winPercentage: Double): String {
        return percentFormat.format(winPercentage)
    }
}