package com.example.data.model

data class Channel(
    val id: String,
    val name: String,
    val logoUrl: String,
    val streamUrl: String,
    val category: String, // e.g. "Sports", "Live TV", "Movies", "Bangla", "International", "Kids", "Entertainment", "News", "Music"
    val country: String = "Global",
    val language: String = "English",
    val isLive: Boolean = true,
    val description: String = "",
    val isFavorite: Boolean = false,
    val posterUrl: String = ""
)
