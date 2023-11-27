package com.example.playlistmaker2023

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker2023.TrackViewHolder

class TrackAdapter(private val trackList: MutableList<Track>, private val searchHistory: SearchHistory) : RecyclerView.Adapter<TrackViewHolder>() {

    private var trackClickListener: OnTrackClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return TrackViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val track = trackList[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            trackClickListener?.onTrackClick(track)
        }
    }

    override fun getItemCount() = trackList.size

    fun updateList(tracks: List<Track>) {
        trackList.clear()
        trackList.addAll(tracks)
        notifyDataSetChanged()
    }


    fun setOnTrackClickListener(listener: OnTrackClickListener) {
        this.trackClickListener = listener
    }

    interface OnTrackClickListener {
        fun onTrackClick(track: Track)
    }
}