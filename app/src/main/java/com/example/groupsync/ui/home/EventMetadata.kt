package com.example.groupsync.ui.home

import android.hardware.biometrics.BiometricManager.Strings

data class EventMetadata(val id: String = "", val title: String = "", val subtitle: String = "", val image: String = "", val time: String = "", val carpoolLink: String = "", val inviteCode: String = "", val users: List<String> = listOf() ) {
    private var mId: String? = id
    private var mTitle: String? = title
    private var mSubtitle: String? = subtitle
    private var mCoverImage: String? = image
    private var mTime: String? = time
    private var mCarpoolLink: String? = carpoolLink
    private var mInviteCode: String? = inviteCode
    private var mUsers: List<String>? = users
}