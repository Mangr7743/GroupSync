package com.example.groupsync.ui.home

data class EventMetadata(val title: String = "", val subtitle: String = "", val image: String = "") {
    private var mTitle: String? = title
    private var mSubtitle: String? = subtitle
    private var mCoverImage: String? = image
}