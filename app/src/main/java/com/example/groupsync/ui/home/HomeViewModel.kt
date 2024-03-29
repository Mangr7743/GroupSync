package com.example.groupsync.ui.home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import java.net.URL
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _text = MutableLiveData<String>().apply {
        value = "Tap the + button to create an event"
    }
    private val _events = MutableLiveData<List<EventMetadata>>().apply {
        value = listOf()
    }
    val text: LiveData<String> = _text
    val events: LiveData<List<EventMetadata>> = _events;

    init {
        fetchDataFromFirestore()
    }

    public fun update() {

    }

    public fun fetchDataFromFirestore() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        db.collection("events")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { result ->
                val eventsList: MutableList<EventMetadata> = mutableListOf()
                for (document in result) {
                    eventsList.add(
                        EventMetadata(
                            document.id,
                            document.data["title"].toString(),
                            document.data["description"].toString(),
                            document.data["imageUrl"].toString(),
                        )
                    )
                }
                _events.postValue(eventsList)
            }
            .addOnFailureListener { exception ->
                Log.e("HELP", "Error getting documents: ", exception)
            }
    }
}