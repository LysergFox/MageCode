package com.kitsune.magecode.view.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.kitsune.magecode.R
import com.kitsune.magecode.controller.App
import com.kitsune.magecode.model.enums.SortOption
import com.kitsune.magecode.model.Account

class ScoreboardActivity : AppCompatActivity() {

    private lateinit var playerList: LinearLayout
    private lateinit var sortSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        playerList = findViewById(R.id.scoreboard_container)
        sortSpinner = findViewById(R.id.sort_spinner)

        setupSortSpinner()
        loadPlayers(SortOption.XP)
    }

    private fun setupSortSpinner() {
        val sortOptions = SortOption.entries.toTypedArray()
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sortOptions.map { it.name.lowercase().replaceFirstChar(Char::titlecase) }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedSort = sortOptions[position]
                loadPlayers(selectedSort)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadPlayers(sortBy: SortOption) {
        App.instance.userRepo.fetchTopPlayers(
            sortBy = sortBy,
            onSuccess = { players ->
                playerList.removeAllViews()
                players.forEachIndexed { index, player ->
                    val view = createPlayerView(index + 1, player)
                    playerList.addView(view)
                }
            },
            onFailure = { error ->
                Toast.makeText(this, "Error: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun createPlayerView(rank: Int, player: Account): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.scoreboard_item, playerList, false)
        val firstName = player.displayName.trim().split(" ").firstOrNull() ?: player.displayName

        view.findViewById<TextView>(R.id.player_name).text = firstName
        view.findViewById<TextView>(R.id.player_xp).text = "${player.xp}"
        view.findViewById<TextView>(R.id.player_level).text = "${player.level}"
        view.findViewById<TextView>(R.id.player_streak).text = "${player.streak}"

        return view
    }
}
