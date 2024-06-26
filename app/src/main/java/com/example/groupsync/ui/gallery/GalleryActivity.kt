package com.example.groupsync.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.groupsync.R
import com.example.groupsync.databinding.ActivityGalleryBinding
import com.example.groupsync.ui.gallery.GalleryFragment
import com.example.groupsync.ui.home.EventMetadata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class GalleryActivity: AppCompatActivity() {
    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firestoreId = intent.getStringExtra("firestoreId")

        if (!firestoreId.isNullOrEmpty()) {
            // Set up navigation buttons for Gallery and Images
            val bundle = Bundle()
            bundle.putString("firestoreId", firestoreId)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Upload Event Images"

            val mFrag = GalleryFragment()
            mFrag.arguments = bundle
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.gallery_fragment, mFrag)
            fragmentTransaction.commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Navigate back when Up button is pressed
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}