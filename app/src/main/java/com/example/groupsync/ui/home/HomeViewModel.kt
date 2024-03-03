package com.example.groupsync.ui.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    private val _cards = MutableLiveData<List<EventMetadata>>().apply {
        value = listOf(
            EventMetadata("title1", "subtitle1"),
            EventMetadata("title2", "subtitle2"))
    }
    val text: LiveData<String> = _text
    val cards: LiveData<List<EventMetadata>> = _cards;


}