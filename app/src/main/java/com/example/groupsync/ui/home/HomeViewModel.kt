package com.example.groupsync.ui.home

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.net.URL
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _text = MutableLiveData<String>().apply {
        value = "Tap the + button to create an event"
    }
    private val _events = MutableLiveData<List<EventMetadata>>().apply {
        value = listOf(
            EventMetadata(
                "title1",
                "subtitle1",
                "https://png.pngtree.com/png-vector/20220724/ourmid/pngtree-people-looking-nature-mountain-tourist-png-image_6063213.png"
            ),
            EventMetadata(
                "title2",
                "subtitle2",
                "https://png.pngtree.com/png-vector/20220724/ourmid/pngtree-people-looking-nature-mountain-tourist-png-image_6063213.png"
            )
        )
    }
    val text: LiveData<String> = _text
    val events: LiveData<List<EventMetadata>> = _events;

    init {
        fetchDataFromFirestore()
    }

    public fun update() {

    }

    public fun fetchDataFromFirestore() {
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val eventsList: MutableList<EventMetadata> = mutableListOf()
                for (document in result) {
                    eventsList.add(
                        EventMetadata(
                            document.data["title"].toString(),
                            document.data["description"].toString(),
                            document.data["imageUrl"].toString(),
                        )
                    )
                }
                _events.postValue(eventsList);
            }
            .addOnFailureListener { exception ->
                Log.d("HELP", "Error getting documents: ", exception)
            }
    }
}