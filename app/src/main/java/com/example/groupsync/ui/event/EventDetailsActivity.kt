package com.example.groupsync.ui.event

import android.Manifest
import com.example.groupsync.R
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.groupsync.databinding.ActivityEventdetailsBinding
import com.example.groupsync.ui.gallery.GalleryActivity
import com.example.groupsync.ui.gallery.ImagesActivity
import com.example.groupsync.ui.map.MapActivity
import com.example.groupsync.ui.home.EventMetadata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class EventDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEventdetailsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout for this fragment
        binding = ActivityEventdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Prompt Location Services
        getLastLocation()

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
            binding.mapsButton.setOnClickListener {
                val intent: Intent = Intent(
                    this,
                    MapActivity::class.java
                )
                intent.putExtra("firestoreId", firestoreId)
                startActivity(intent)
            }
            binding.timeButton.setOnClickListener {
                getBestTime(firestoreId)
                return@setOnClickListener
            }
        }
    }

    private fun getBestTime(id: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            return;
        }
        db.collection("events").document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if (document.contains("users")) {
                        val users = document.get("users") as List<String>
                        var avMaps = mutableListOf<Map<String,Boolean>>()
                        var completedQueries = 0

                        for (user in users) {
                            var currUser = db.collection("users").document(user)

                            currUser.get()
                                .addOnSuccessListener { userDocument ->
                                    if (userDocument.exists()) {
                                        var currAvailability = userDocument.get("availability") as Map<String, Boolean>
                                        avMaps.add(currAvailability)
                                    }
                                    completedQueries++  // Increment counter
                                    // Run algo
                                    if (completedQueries == users.size) {
                                        // All queries completed, proceed with the algorithm
                                        val timeSlot = findFirstFreeSpace(avMaps)
                                        if (timeSlot != "N/A") {

                                           db.collection("events").document(id)
                                               .update(
                                                   hashMapOf(
                                                       "time" to timeSlot
                                                   ) as Map<String, Any>
                                               ).addOnSuccessListener {
                                                   binding.timeTitle.text = timeSlot
                                                   Toast.makeText(this, "Event Time Found", Toast.LENGTH_LONG).show()
                                               }
                                               .addOnFailureListener {
                                                   Toast.makeText(this, "Could not update database", Toast.LENGTH_LONG).show()
                                               }
                                        } else {
                                            Toast.makeText(this, "No Time Found", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                                }


                        }

                    }
                }

            }
            .addOnFailureListener { e ->
                // Handle failure to fetch event data
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun findFirstFreeSpace(userMaps: MutableList<Map<String, Boolean>>): String {
        if (userMaps.isEmpty()) {
            return "N/A"
        }
        // Combine the keys of all maps to find the common timeslots
        val temp = userMaps.map { it.keys }.reduce { acc, set -> acc.intersect(set) }
        val dayOrder = listOf("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday")
        val hoursOfDay = listOf("6pm", "7pm", "8pm", "9pm", "10pm", "11pm", "12am")
        val commonTimeslots = temp.toList().sortedWith(compareBy({ dayOrder.indexOf(it.split("/")[0]) }, { hoursOfDay.indexOf(it.split("/")[1]) }))

        // Iterate through the common timeslots
        for (timeslot in commonTimeslots) {
            // Check if all users are available at this timeslot
            if (userMaps.all { !it[timeslot]!! }) {
                // Return the first free timeslot found
                return timeslot
            }
        }

        // No free timeslots found
        return "N/A"
    }

    private fun getLastLocation() {
        // Check if location permissions are granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request the missing permissions
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        // Permissions are granted, proceed to get the location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            // In some rare situations, this can be null.
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                // Use the latitude and longitude as needed
                updateUserLocationInFirebase(latitude, longitude)
            } else {
                // Handle the case where location is null
                // You might want to request a new location update here
            }
        }
    }

    private fun updateUserLocationInFirebase(latitude: Double, longitude: Double) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid  // Make sure the user is authenticated

        if (userId != null) {
            val userLocation = mapOf(
                "latitude" to latitude,
                "longitude" to longitude
            )


            db.collection("users").document(userId)
                .update(userLocation)
                .addOnSuccessListener {
                    // Successfully updated Firestore
                    Toast.makeText(this, "Location Provided", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    // Handle errors
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
        }
    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
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
                        document.data?.get("imageUrl").toString(),
                        document.data?.get("time").toString()
                    )

                    supportActionBar?.title = metadata.title
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    binding.detailsTitle.text = metadata.title
                    binding.detailsDesc.text = metadata.subtitle
                    binding.timeTitle.text = metadata.time
                    Picasso.get().load(metadata.image).into(binding.detailsImage)
                    return@addOnSuccessListener // Exit the loop after finding the matching document
                }
                .addOnFailureListener { e ->
                    // Handle failure to fetch event data
                }
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
