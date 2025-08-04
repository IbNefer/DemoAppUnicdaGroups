package com.example.demounicdagroups.features.chat

data class Message (
    val id: String = "",
    val senderId: String = "",
    val receriverId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val imageUrl: String? = null
)