package com.example.groupsync.ui.gallery

data class Upload(val name: String = "", val imageUrl: String = "") {
    private var mName: String? = name
    private var mImageUrl: String? = imageUrl

}