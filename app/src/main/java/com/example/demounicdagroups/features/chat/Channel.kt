package com.example.demounicdagroups.features.chat

data class Channel (
    val id: String,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)