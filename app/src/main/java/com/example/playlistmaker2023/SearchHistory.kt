package com.example.playlistmaker2023

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences : SharedPreferences) {
    companion object {
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }

    fun hasTracksInHistory(): Boolean {
        val historyList = getSearchHistory()
        return historyList.isNotEmpty()
    }

    fun saveSearch(track: Track) {
        val historyList = getSearchHistory()
        historyList.removeAll { it.trackId == track.trackId }
        historyList.add(0, track)
        if (historyList.size > MAX_HISTORY_SIZE) {
            historyList.removeAt(MAX_HISTORY_SIZE)
        }
        saveHistoryList(historyList)
    }

    fun getSearchHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, "")
        return if (json.isNullOrEmpty()) {
            ArrayList()
        } else {
            val type = object : TypeToken<ArrayList<Track>>() {}.type
            Gson().fromJson(json, type)
        }
    }

    fun clearSearchHistory() {
        saveHistoryList(ArrayList())
    }

    private fun saveHistoryList(historyList: ArrayList<Track>) {
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(historyList)
        editor.putString(HISTORY_KEY, json)
        editor.apply()
    }

    fun hasSearch(track: Track): Boolean {
        val historyList = getSearchHistory()
        return historyList.any { it.trackId == track.trackId }
    }
}