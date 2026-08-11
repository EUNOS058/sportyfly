package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Channel
import com.example.data.repository.ChannelRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.UserSettings
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val channelRepository = ChannelRepository(database.channelDao())
    private val settingsRepository = SettingsRepository(application)

    val settings: StateFlow<UserSettings> = settingsRepository.settings

    val allChannels: StateFlow<List<Channel>> = channelRepository.channelsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val favoriteChannels: StateFlow<List<Channel>> = channelRepository.favoriteChannelsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recentlyWatchedChannels: StateFlow<List<Channel>> = channelRepository.recentlyWatchedChannelsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _activeChannel = MutableStateFlow<Channel?>(null)
    val activeChannel: StateFlow<Channel?> = _activeChannel.asStateFlow()

    private val _isPlayerFullscreen = MutableStateFlow(false)
    val isPlayerFullscreen: StateFlow<Boolean> = _isPlayerFullscreen.asStateFlow()

    init {
        // Set initial featured/active channel once channels load
        viewModelScope.launch {
            allChannels.filter { it.isNotEmpty() }.firstOrNull()?.let { channels ->
                if (_activeChannel.value == null) {
                    val lastId = settings.value.lastChannelId
                    val channelToSelect = channels.find { it.id == lastId } ?: channels.first()
                    _activeChannel.value = channelToSelect
                }
            }
        }
    }

    val filteredChannels: StateFlow<List<Channel>> = combine(
        allChannels,
        selectedCategory,
        searchQuery
    ) { channels, category, query ->
        channels.filter { channel ->
            val matchesCategory = (category == "All") || channel.category.equals(category, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    channel.name.contains(query, ignoreCase = true) ||
                    channel.category.contains(query, ignoreCase = true) ||
                    channel.language.contains(query, ignoreCase = true) ||
                    channel.country.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun playChannel(channel: Channel) {
        _activeChannel.value = channel
        viewModelScope.launch {
            channelRepository.recordWatchHistory(channel.id)
            if (settings.value.rememberLastChannel) {
                settingsRepository.updateLastChannelId(channel.id)
            }
        }
    }

    fun playNextChannel() {
        val currentList = allChannels.value
        if (currentList.isEmpty()) return
        val currentIndex = currentList.indexOfFirst { it.id == _activeChannel.value?.id }
        val nextIndex = if (currentIndex != -1) (currentIndex + 1) % currentList.size else 0
        playChannel(currentList[nextIndex])
    }

    fun playPreviousChannel() {
        val currentList = allChannels.value
        if (currentList.isEmpty()) return
        val currentIndex = currentList.indexOfFirst { it.id == _activeChannel.value?.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else currentList.size - 1
        playChannel(currentList[prevIndex])
    }

    fun toggleFavorite(channel: Channel) {
        viewModelScope.launch {
            channelRepository.toggleFavorite(channel.id, channel.isFavorite)
        }
    }

    fun togglePlayerFullscreen() {
        _isPlayerFullscreen.value = !_isPlayerFullscreen.value
    }

    fun setPlayerFullscreen(enabled: Boolean) {
        _isPlayerFullscreen.value = enabled
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            channelRepository.clearWatchHistory()
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            channelRepository.clearFavorites()
        }
    }

    fun updateTheme(mode: String) {
        settingsRepository.updateThemeMode(mode)
    }

    fun updateAutoLandscape(enabled: Boolean) {
        settingsRepository.updateAutoLandscape(enabled)
    }

    fun updateRememberLastChannel(enabled: Boolean) {
        settingsRepository.updateRememberLastChannel(enabled)
    }

    fun updateAutoPlay(enabled: Boolean) {
        settingsRepository.updateAutoPlay(enabled)
    }

    fun updateVideoQuality(quality: String) {
        settingsRepository.updateVideoQuality(quality)
    }
}
