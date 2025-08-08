package com.example.demounicdagroups.features.group

import androidx.annotation.DrawableRes
import com.example.demounicdagroups.R
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class GroupInfo(
    @DocumentId val id: String = "",
    val name: String = "",
    val courseCode: String = "",
    val groupDetail: String = "",
    val memberCount: Long = 0,
    val members: List<String> = emptyList(),
    @DrawableRes val iconResId: Int = R.drawable.group_out,

    val creatorUid: String = "",
    val creatorName: String = "",
    @ServerTimestamp val createdAt: Date? = null
)