package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserSettings(
    val themeMode: String = "Dark", // "Dark", "Light", "System"
    val autoLandscapeFullscreen: Boolean = true,
    val rememberLastChannel: Boolean = true,
    val lastChannelId: String = "sports_001",
    val autoPlay: Boolean = true,
    val videoQuality: String = "Auto"
)

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("sportyfly_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<UserSettings> = _settings.asStateFlow()

    private fun loadSettings(): UserSettings {
        return UserSettings(
            themeMode = prefs.getString("theme_mode", "Dark") ?: "Dark",
            autoLandscapeFullscreen = prefs.getBoolean("auto_landscape", true),
            rememberLastChannel = prefs.getBoolean("remember_last_channel", true),
            lastChannelId = prefs.getString("last_channel_id", "sports_001") ?: "sports_001",
            autoPlay = prefs.getBoolean("auto_play", true),
            videoQuality = prefs.getString("video_quality", "Auto") ?: "Auto"
        )
    }

    fun updateThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun updateAutoLandscape(enabled: Boolean) {
        prefs.edit().putBoolean("auto_landscape", enabled).apply()
        _settings.value = _settings.value.copy(autoLandscapeFullscreen = enabled)
    }

    fun updateRememberLastChannel(enabled: Boolean) {
        prefs.edit().putBoolean("remember_last_channel", enabled).apply()
        _settings.value = _settings.value.copy(rememberLastChannel = enabled)
    }

    fun updateLastChannelId(channelId: String) {
        prefs.edit().putString("last_channel_id", channelId).apply()
        _settings.value = _settings.value.copy(lastChannelId = channelId)
    }

    fun updateAutoPlay(enabled: Boolean) {
        prefs.edit().putBoolean("auto_play", enabled).apply()
        _settings.value = _settings.value.copy(autoPlay = enabled)
    }

    fun updateVideoQuality(quality: String) {
        prefs.edit().putString("video_quality", quality).apply()
        _settings.value = _settings.value.copy(videoQuality = quality)
    }
}
