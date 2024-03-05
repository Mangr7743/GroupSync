package com.example.groupsync.ui.gallery

data class Upload(
    var name: String = "",
    var imageUrl: String = ""
) {
    init {
        if (name.trim().isEmpty()) {
            name = "No Name"
        }
    }
}
