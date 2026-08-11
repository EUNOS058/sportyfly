package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val channelId: String,
    val watchedAt: Long = System.currentTimeMillis()
)
