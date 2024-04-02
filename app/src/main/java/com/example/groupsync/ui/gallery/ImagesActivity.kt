package com.example.groupsync.ui.gallery

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.groupsync.R
import com.example.groupsync.databinding.ActivityImagesBinding
import com.example.groupsync.ui.gallery.ImagesFragment

class ImagesActivity: AppCompatActivity() {
    private lateinit var binding: ActivityImagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firestoreId = intent.getStringExtra("firestoreId")

        if (!firestoreId.isNullOrEmpty()) {
            // Set up navigation buttons for Gallery and Images
            val bundle = Bundle()
            bundle.putString("firestoreId", firestoreId)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "View Event Images"

            val mFrag = ImagesFragment()
            mFrag.arguments = bundle
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.images_fragment, mFrag)
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