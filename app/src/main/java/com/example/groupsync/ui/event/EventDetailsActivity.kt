package com.example.groupsync.ui.event

import com.example.groupsync.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.groupsync.databinding.ActivityEventdetailsBinding
import com.example.groupsync.ui.gallery.GalleryActivity
import com.example.groupsync.ui.gallery.ImagesActivity
import com.example.groupsync.ui.home.EventMetadata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class EventDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventdetailsBinding

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding = ActivityEventdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firestoreId = intent.getStringExtra("firestoreId")
        if (!firestoreId.isNullOrEmpty()) {
            val metadata = fetchFirestoreEventData(firestoreId)

            // Set up navigation buttons for Gallery and Images
            val bundle = Bundle()
            bundle.putString("firestoreId", firestoreId)

            val mFrag = InviteFragment()
            mFrag.arguments = bundle
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.invite_container, mFrag)
            fragmentTransaction.commit()

            binding.galleryButton.setOnClickListener {
                val intent: Intent = Intent(
                    this,
                    GalleryActivity::class.java
                )
                intent.putExtra("firestoreId", firestoreId)
                startActivity(intent)
            }
            binding.imagesButton.setOnClickListener {
                val intent: Intent = Intent(
                    this,
                    ImagesActivity::class.java
                )
                intent.putExtra("firestoreId", firestoreId)
                startActivity(intent)
            }
        }
    }

    private fun fetchFirestoreEventData(id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            db.collection("events").document(id)
                .get()
                .addOnSuccessListener { document ->
                    val metadata = EventMetadata(
                        id,
                        document.data?.get("title").toString(),
                        document.data?.get("description").toString(),
                        document.data?.get("imageUrl").toString()
                    )

                    supportActionBar?.title = metadata.title
                    binding.detailsTitle.text = metadata.title
                    binding.detailsDesc.text = metadata.subtitle
                    Picasso.get().load(metadata.image).into(binding.detailsImage)
                    return@addOnSuccessListener // Exit the loop after finding the matching document
                }
                .addOnFailureListener { e ->
                    // Handle failure to fetch event data
                }
        }
    }
}