package com.example.groupsync.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.groupsync.R
import com.squareup.picasso.Picasso

class ImageAdapter(private val mContext: Context, private val mUploads: List<Upload>): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(v)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uploadCurrent: Upload = mUploads[position]
        holder.textViewName.text = uploadCurrent.name
        Picasso.get()
            .load(uploadCurrent.imageUrl)
            .fit()
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return mUploads.size
    }

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewName: TextView = itemView.findViewById(R.id.text_view_name)
        var imageView: ImageView = itemView.findViewById(R.id.image_view_upload)
    }
}