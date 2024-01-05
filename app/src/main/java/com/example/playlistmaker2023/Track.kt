package com.example.playlistmaker2023

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class Track (
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val trackId: Int,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String
) : Serializable {
    val artworkUrl512: String
        get() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

    fun formattedTrackTime(): String {
        val simpleDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return simpleDateFormat.format(trackTimeMillis)
    }
}