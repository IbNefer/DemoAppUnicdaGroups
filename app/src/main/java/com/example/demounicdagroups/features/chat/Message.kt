package com.example.demounicdagroups.features.chat

data class Message (
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val senderImgae: String? = null,
    val imageUrl: String? = null
)