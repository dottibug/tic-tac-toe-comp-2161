package com.example.tictactoe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

// StandingsListViewAdapter class for customizing the layout of each list item in the standings list view
class StandingsListViewAdapter(context: Context, playerList: List<Player>) : ArrayAdapter<Player>(context, 0, playerList) {

    // Override the getView() method to customize the layout for each list item
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflate the custom layout for the list item
        val itemView = convertView ?: LayoutInflater.from(context).inflate(R.layout.custom_list_item, parent, false)
        val item = getItem(position)

        // name, totalGames, losses, ties, wins, winPercentage, lastPlayed
        val name: TextView = itemView.findViewById(R.id.textViewListItemPlayer)
        name.text = item?.name

        val total: TextView = itemView.findViewById(R.id.textViewListItemTotal)
        total.text = item?.totalGames.toString()

        val losses: TextView = itemView.findViewById(R.id.textViewListItemLosses)
        losses.text = item?.losses.toString()

        val ties: TextView = itemView.findViewById(R.id.textViewListItemTies)
        ties.text = item?.ties.toString()

        val wins: TextView = itemView.findViewById(R.id.textViewListItemWins)
        wins.text = item?.wins.toString()

        val winPercentage: TextView = itemView.findViewById(R.id.textViewListItemWinPercentage)
        winPercentage.text = item?.winPercentage

        val lastPlayed: TextView = itemView.findViewById(R.id.textViewListItemLastPlayed)
        lastPlayed.text = item?.lastPlayed

        return itemView
    }
}