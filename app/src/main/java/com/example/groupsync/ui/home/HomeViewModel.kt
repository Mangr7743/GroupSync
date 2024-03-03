package com.example.groupsync.ui.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.net.URL

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Tap the + button to create an event"
    }
    private val _events = MutableLiveData<List<EventMetadata>>().apply {
        value = listOf(
            EventMetadata("title1", "subtitle1", "https://png.pngtree.com/png-vector/20220724/ourmid/pngtree-people-looking-nature-mountain-tourist-png-image_6063213.png"),
            EventMetadata("title2", "subtitle2", "https://png.pngtree.com/png-vector/20220724/ourmid/pngtree-people-looking-nature-mountain-tourist-png-image_6063213.png"))
    }
    val text: LiveData<String> = _text
    val events: LiveData<List<EventMetadata>> = _events;

    public fun update() {

    }
}