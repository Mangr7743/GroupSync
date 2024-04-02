package com.example.groupsync.ui.gallery

import android.os.Bundle
import android.util.Log
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

            val mFrag = ImagesFragment()
            mFrag.arguments = bundle
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.images_fragment, mFrag)
            fragmentTransaction.commit()
        }
    }
}