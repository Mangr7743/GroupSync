package com.example.groupsync.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupsync.R
import com.example.groupsync.ui.event.EventDetailsActivity
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso


class EventsAdapter(private val mContext: Context, private val mEvents: List<EventMetadata>, private val homeViewModel: HomeViewModel): RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsAdapter.EventViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventsAdapter.EventViewHolder, position: Int) {
        val eventCurrent: EventMetadata = mEvents[position]
        holder.textViewTitle.text = eventCurrent.title
        holder.textViewSubtitle.text = eventCurrent.subtitle

        // Convert image url to bitmap
        Picasso.get().load(eventCurrent.image).into(holder.coverImageView)

        holder.containerView.setOnClickListener {
            val intent = Intent(mContext, EventDetailsActivity::class.java)
            intent.putExtra("firestoreId", eventCurrent.id)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mEvents.size
    }
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.event_title)
        var textViewSubtitle: TextView = itemView.findViewById(R.id.event_subtitle)
        var coverImageView: ShapeableImageView = itemView.findViewById(R.id.event_image)
        var containerView: MaterialCardView = itemView.findViewById(R.id.event_container)
    }
}

