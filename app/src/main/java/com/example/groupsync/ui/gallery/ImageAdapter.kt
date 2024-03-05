package com.example.groupsync.ui.gallery

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.groupsync.databinding.ImageItemBinding
import com.squareup.picasso.Picasso

class ImageAdapter(private val context: Context, private val uploads: List<Upload>)
    : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val upload = uploads[position]
        with(holder.binding) {
            textViewName.text = upload.name
            if (upload.imageUrl.isNotEmpty()) {
                Picasso.get()
                    .load(upload.imageUrl)
                    .fit()
                    .centerCrop()
                    .into(holder.binding.imageViewUpload)
            } else {
                // Handle the case where imageUrl is null or empty, e.g., set a default image
            }

        }
    }

    override fun getItemCount() = uploads.size


    class ImageViewHolder(val binding: ImageItemBinding) : RecyclerView.ViewHolder(binding.root)
}