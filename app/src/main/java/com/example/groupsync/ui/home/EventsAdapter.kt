package com.example.groupsync.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupsync.R

class EventsAdapter(private val mContext: Context, private val mEvents: List<EventMetadata>): RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapter.EventViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventsAdapter.EventViewHolder, position: Int) {
        val eventCurrent: EventMetadata = mEvents[position]
        holder.textViewTitle.text = eventCurrent.title
        holder.textViewSubtitle.text = eventCurrent.subtitle
    }

    override fun getItemCount(): Int {
        return mEvents.size
    }
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.event_title)
        var textViewSubtitle: TextView = itemView.findViewById(R.id.event_subtitle)
    }
}

