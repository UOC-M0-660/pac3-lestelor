package edu.uoc.pac3.twitch.streams

import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.pac3.R
import edu.uoc.pac3.data.streams.Stream
import edu.uoc.pac3.data.streams.StreamsResponse


/**
 * Adapter for a list of Books.
 */

class TwitchListAdapter(private var streams: List<Stream>) : RecyclerView.Adapter<TwitchListAdapter.ViewHolder>() {

    private fun getStream(position: Int): Stream? {
        return streams[position]
    }

    fun setStreams(streams: List<Stream>) {
        this.streams = streams
        // Reloads the RecyclerView with new adapter data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    // Creates View Holder for re-use
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_stream_list, parent, false)
        return ViewHolder(view)
    }

    // Binds re-usable View for a given position
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stream = getStream(position)
        holder.usernameView.text= stream?.userName
        holder.titleView.text= stream?.title
        holder.urlView.text= stream?.thumbnailUrl

        // Set View Click Listener
        holder.view.setOnClickListener { v ->
            val context = v.context
            /*val animation = ActivityOptions.makeCustomAnimation(holder.view.context, R.anim.translate_in_bottom,
                    R.anim.translate_out_bottom).toBundle()
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra(BookDetailFragment.ARG_ITEM_ID, book.uid)
            context.startActivity(intent, animation)*/
        }
    }

    // Returns total items in Adapter
    override fun getItemCount(): Int {
        return streams.size
    }

    // Holds an instance to the view for re-use
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val usernameView: TextView = view.findViewById(R.id.tv_username)
        val titleView: TextView = view.findViewById(R.id.tv_title)
        val urlView: TextView = view.findViewById(R.id.tv_url)
    }



}
