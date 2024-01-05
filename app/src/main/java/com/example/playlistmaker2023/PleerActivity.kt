package com.example.playlistmaker2023

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class PleerActivity : AppCompatActivity() {

    private lateinit var menuButton: ImageButton
    private lateinit var placeholder: ImageView
    private lateinit var pleerTrack: TextView
    private lateinit var pleerGroup: TextView
    private lateinit var pleerInfoTimeCounter: TextView
    private lateinit var pleerInfoTimelineCount: TextView
    private lateinit var pleerInfoAlbumName: TextView
    private lateinit var pleerInfoAlbum: TextView
    private lateinit var pleerInfoYearCount: TextView
    private lateinit var pleerInfoRock: TextView
    private lateinit var pleerInfoCountryName: TextView
    private lateinit var pleerButtonPlay: ImageButton
    private lateinit var pleerButtonAdd: ImageButton
    private lateinit var pleerButtonLike: ImageButton

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pleer)

        track = intent.serializable<Track>(Constants.TRACK) ?: throw IllegalArgumentException("Трека нет")

        menuButton = findViewById(R.id.menu_button)
        placeholder = findViewById(R.id.placeholder)
        pleerTrack = findViewById(R.id.pleer_track)
        pleerGroup = findViewById(R.id.pleer_group)
        pleerInfoTimeCounter = findViewById(R.id.pleer_info_time_counter)
        pleerInfoTimelineCount = findViewById(R.id.pleer_info_timeline_count)
        pleerInfoAlbumName = findViewById(R.id.pleer_info_album_name)
        pleerInfoAlbum = findViewById(R.id.pleer_info_album)
        pleerInfoYearCount = findViewById(R.id.pleer_info_year_count)
        pleerInfoRock = findViewById(R.id.pleer_info_rock)
        pleerInfoCountryName = findViewById(R.id.pleer_info_country_name)
        pleerButtonPlay = findViewById(R.id.button_pleer_play)
        pleerButtonAdd = findViewById(R.id.button_pleer_add)
        pleerButtonLike = findViewById(R.id.button_pleer_like)

        val pleerIntent = Intent(this@PleerActivity, PleerActivity::class.java)
        pleerIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(pleerIntent)

        menuButton.setOnClickListener {
            finish()
        }

        displayTrackInfo()

    }

    private fun displayTrackInfo() {
        pleerTrack.text = track.trackName
        pleerGroup.text = track.artistName
        pleerInfoTimelineCount.text = track.formattedTrackTime()
        pleerInfoYearCount.text = track.releaseDate.subSequence(0, 4)
        pleerInfoRock.text = track.primaryGenreName
        pleerInfoCountryName.text = track.country
        if (track.collectionName.isNotEmpty()) {
            pleerInfoAlbumName.text = track.collectionName
        } else {
            pleerInfoAlbumName.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.artworkUrl512)
            .placeholder(R.drawable.placeholder_main)
            .centerInside()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.space)))
            .into(placeholder)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}