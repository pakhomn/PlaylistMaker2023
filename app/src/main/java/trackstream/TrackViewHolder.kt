package trackstream

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker2023.R

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val trackName: TextView = itemView.findViewById(R.id.sourceName)
    val artistName: TextView = itemView.findViewById(R.id.bandName)
    val trackTime: TextView = itemView.findViewById(R.id.timeTrack)
    val pictureBand: ImageView = itemView.findViewById(R.id.trackPicture)

    fun bind(item: Track) {
        trackName.text = item.trackName
        artistName.text = item.artistName
        trackTime.text = item.trackTime
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.plug)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.half_half_space)))
            .into(pictureBand)
    }
}