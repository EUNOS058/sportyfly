package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    @Query("SELECT channelId FROM favorites ORDER BY addedAt DESC")
    fun getFavoriteChannelIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE channelId = :channelId")
    suspend fun removeFavorite(channelId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE channelId = :channelId)")
    fun isFavorite(channelId: String): Flow<Boolean>

    @Query("SELECT channelId FROM watch_history ORDER BY watchedAt DESC LIMIT 20")
    fun getRecentlyWatchedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordWatchHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history")
    suspend fun clearWatchHistory()

    @Query("DELETE FROM favorites")
    suspend fun clearFavorites()
}
