package com.example.data.repository

import com.example.data.local.ChannelDao
import com.example.data.local.FavoriteEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.Channel
import com.example.data.sample.SampleChannels
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class ChannelRepository(private val channelDao: ChannelDao) {

    // Base raw channels list (Can be modified or fetched from remote source later)
    private val rawChannels = SampleChannels.DEFAULT_CHANNELS

    // Flow of channels augmented with real-time Room favorite status
    val channelsFlow: Flow<List<Channel>> = channelDao.getFavoriteChannelIds()
        .map { favoriteIds ->
            val favSet = favoriteIds.toSet()
            rawChannels.map { channel ->
                channel.copy(isFavorite = favSet.contains(channel.id))
            }
        }

    // Flow of favorited channels only
    val favoriteChannelsFlow: Flow<List<Channel>> = channelsFlow.map { channels ->
        channels.filter { it.isFavorite }
    }

    // Flow of recently watched channels (up to 20)
    val recentlyWatchedChannelsFlow: Flow<List<Channel>> = channelDao.getRecentlyWatchedIds()
        .combine(channelsFlow) { historyIds, channels ->
            val channelMap = channels.associateBy { it.id }
            historyIds.mapNotNull { id -> channelMap[id] }
        }

    suspend fun toggleFavorite(channelId: String, currentFavorite: Boolean) {
        if (currentFavorite) {
            channelDao.removeFavorite(channelId)
        } else {
            channelDao.addFavorite(FavoriteEntity(channelId = channelId))
        }
    }

    suspend fun recordWatchHistory(channelId: String) {
        channelDao.recordWatchHistory(WatchHistoryEntity(channelId = channelId))
    }

    suspend fun clearWatchHistory() {
        channelDao.clearWatchHistory()
    }

    suspend fun clearFavorites() {
        channelDao.clearFavorites()
    }

    fun getChannelById(id: String, channels: List<Channel>): Channel? {
        return channels.find { it.id == id } ?: rawChannels.find { it.id == id }
    }
}
