package edu.uoc.pac3.twitch.streams

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.uoc.pac3.R
import edu.uoc.pac3.data.streams.Stream
import edu.uoc.pac3.data.streams.StreamsResponse


/**
 * Adapter for a list of Books.
 */

class TwitchListAdapter(private var streams: StreamsResponse, itemStart: Int, itemCount: Int) : RecyclerView.Adapter<TwitchListAdapter.ViewHolder>() {

    private fun getStream(position: Int): Stream? {
        return streams.data?.get(position)
    }

    fun setStreams(streams: StreamsResponse, itemStart: Int, itemCount: Int) {
        this.streams = streams


        // Reloads the RecyclerView with new adapter data
        //notifyDataSetChanged()

        // Instead of reloading the whole list of Streams, here only the 20 additional are informed to the Adapter to be loaded
        notifyItemRangeInserted(itemStart, itemCount)
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
        val thumbnailUrl = stream?.thumbnailUrl?.replace("{width}",
            "400")?.replace("{height}", "200")
        holder.urlView.text= thumbnailUrl
        Picasso.get().load(thumbnailUrl).into(holder.imageView)
        holder.imageView.contentDescription = thumbnailUrl

        // Set View Click Listener
        holder.view.setOnClickListener { v ->
            val context = v.context
            // Here it is possible to add a listener when clicking any of the streams. An animation could be used
            /*val animation = ActivityOptions.makeCustomAnimation(holder.view.context, R.anim.translate_in_bottom,
                    R.anim.translate_out_bottom).toBundle()
            val intent = Intent(context, BookDetailActivity::class.java)
            intent.putExtra(BookDetailFragment.ARG_ITEM_ID, book.uid)
            context.startActivity(intent, animation)*/
        }

    }

    // Returns total items in Adapter
    override fun getItemCount(): Int {
        return streams.data?.size ?: 0
    }

    // Holds an instance to the view for re-use
    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.iv_url)
        val usernameView: TextView = view.findViewById(R.id.tv_username)
        val titleView: TextView = view.findViewById(R.id.tv_title)
        val urlView: TextView = view.findViewById(R.id.tv_url)
    }



}

