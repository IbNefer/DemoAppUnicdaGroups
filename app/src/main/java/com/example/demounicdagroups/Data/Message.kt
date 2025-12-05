package com.example.demounicdagroups.Data

data class Message(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val senderName: String = "",
    val senderProfileUrl: String? = null,
    val imageUrl: String? = null
)