package com.example.groupsync.ui.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.groupsync.databinding.ActivityMapBinding
import com.example.groupsync.ui.home.EventMetadata
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class MapActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMapBinding

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val firestoreId = intent.getStringExtra("firestoreId")
        if (!firestoreId.isNullOrEmpty()) {

            getMapsUrl(firestoreId)

            var webView: WebView = binding.mapView
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (URLUtil.isNetworkUrl(url)) {
                        return false
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true
                }
            }
            webView.settings.javaScriptEnabled = true

            db.collection("events").document(firestoreId).get()
                .addOnSuccessListener { document ->
                    var myMap = document.data?.get("carpoolLink").toString()
                    if (myMap != "") {
                        webView.loadUrl(myMap)
                    }
                }
                .addOnFailureListener {
                    webView.loadUrl("https://maps.google.com")
                }
        }
    }

    private fun getMapsUrl(id: String) {
        db.collection("events").document(id)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    if (document.contains("users")) {
                        val users = document.get("users") as List<String>
                        val carPoolList = mutableListOf<Pair<String, String>>()
                        var completedQueries = 0

                        for (user in users) {
                            var currUser = db.collection("users").document(user)

                            currUser.get()
                                .addOnSuccessListener { userDocument ->
                                    if (userDocument.exists()) {
                                        var lat = userDocument.get("latitude").toString()
                                        var long = userDocument.get("longitude").toString()

                                        carPoolList.add(Pair(lat, long))
                                    }
                                    completedQueries++  // Increment counter
                                    // Run algo
                                    if (completedQueries == users.size) {
                                        val return_url = constructGoogleMapsUrl(carPoolList)

                                        db.collection("events").document(id)
                                            .update(
                                                hashMapOf(
                                                    "carpoolLink" to return_url
                                                ) as Map<String, Any>
                                            ).addOnSuccessListener {
                                                Toast.makeText(this, "Carpool Route Found", Toast.LENGTH_LONG).show()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this, "Could not make route", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                }
                                .addOnFailureListener {

                                }
                        }

                    }

                }
            }
    }

    private fun constructGoogleMapsUrl(locations: MutableList<Pair<String, String>>, travelMode: String = "driving"): String {
        if (locations.isEmpty()) {
            throw IllegalArgumentException("Locations list cannot be empty")
        }

        // Google Maps URL base
        var url = "https://www.google.com/maps/dir/?api=1"

        // Add the first location as the origin
        val origin = locations.first()
        url += "&origin=${origin.first},${origin.second}"

        // Add the last location as the destination if there are at least two locations
        if (locations.size > 1) {
            val destination = locations.last()
            url += "&destination=${destination.first},${destination.second}"
        }

        // Add intermediate locations as waypoints
        if (locations.size > 2) {
            val waypoints = locations.drop(1).dropLast(1).joinToString("|") { "${it.first},${it.second}" }
            url += "&waypoints=$waypoints"
        }

        // Append the travel mode
        url += "&travelmode=$travelMode"

        return url
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
