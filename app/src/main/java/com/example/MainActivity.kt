package com.example

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.SportyFlyBottomBar
import com.example.ui.components.SportyFlyTopBar
import com.example.ui.screens.*
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.SportyFlyTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MainViewModel = viewModel()

            val settings by viewModel.settings.collectAsStateWithLifecycle()
            val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
            val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
            val favoriteChannels by viewModel.favoriteChannels.collectAsStateWithLifecycle()
            val recentlyWatched by viewModel.recentlyWatchedChannels.collectAsStateWithLifecycle()
            val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val activeChannel by viewModel.activeChannel.collectAsStateWithLifecycle()
            val isPlayerFullscreen by viewModel.isPlayerFullscreen.collectAsStateWithLifecycle()

            var currentRoute by remember { mutableStateOf("home") }

            // Auto-landscape mode handling for fullscreen player
            LaunchedEffect(isPlayerFullscreen, settings.autoLandscapeFullscreen) {
                requestedOrientation = if (isPlayerFullscreen && settings.autoLandscapeFullscreen) {
                    ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                } else {
                    ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }

            // Custom Back Handler when on Player screen or search view
            BackHandler(enabled = currentRoute != "home" || isPlayerFullscreen) {
                if (isPlayerFullscreen) {
                    viewModel.setPlayerFullscreen(false)
                } else if (currentRoute == "player") {
                    currentRoute = "home"
                } else if (searchQuery.isNotEmpty()) {
                    viewModel.updateSearchQuery("")
                } else {
                    currentRoute = "home"
                }
            }

            SportyFlyTheme(themeMode = settings.themeMode) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize().background(DarkNavyBackground)) {
                    val isCompact = maxWidth < 600.dp

                    Scaffold(
                        topBar = {
                            if (!isPlayerFullscreen && currentRoute != "player") {
                                SportyFlyTopBar(
                                    selectedTab = when (currentRoute) {
                                        "home" -> selectedCategory
                                        "live" -> "Live TV"
                                        "favorites" -> "Favorites"
                                        "search" -> "Search"
                                        "settings" -> "More"
                                        else -> "Home"
                                    },
                                    onTabSelected = { tab ->
                                        when (tab.lowercase()) {
                                            "home" -> {
                                                currentRoute = "home"
                                                viewModel.selectCategory("All")
                                            }
                                            "live tv", "live" -> {
                                                currentRoute = "live"
                                            }
                                            "favorites" -> {
                                                currentRoute = "favorites"
                                            }
                                            "search" -> {
                                                currentRoute = "search"
                                            }
                                            "settings", "more" -> {
                                                currentRoute = "settings"
                                            }
                                            else -> {
                                                currentRoute = "home"
                                                viewModel.selectCategory(tab)
                                            }
                                        }
                                    },
                                    onSearchClick = { currentRoute = "search" },
                                    onProfileClick = { currentRoute = "settings" },
                                    isCompact = isCompact
                                )
                            }
                        },
                        bottomBar = {
                            if (!isPlayerFullscreen && currentRoute != "player") {
                                SportyFlyBottomBar(
                                    currentRoute = currentRoute,
                                    onNavigate = { route -> currentRoute = route }
                                )
                            }
                        },
                        containerColor = DarkNavyBackground,
                        contentWindowInsets = WindowInsets(0, 0, 0, 0)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentRoute) {
                                "home" -> {
                                    HomeScreen(
                                        channels = filteredChannels,
                                        recentlyWatched = recentlyWatched,
                                        activeChannel = activeChannel,
                                        selectedCategory = selectedCategory,
                                        searchQuery = searchQuery,
                                        autoPlay = settings.autoPlay,
                                        videoQuality = settings.videoQuality,
                                        onCategorySelect = { cat -> viewModel.selectCategory(cat) },
                                        onSearchQueryChange = { query -> viewModel.updateSearchQuery(query) },
                                        onChannelSelect = { channel ->
                                            viewModel.playChannel(channel)
                                            currentRoute = "player"
                                        },
                                        onFavoriteToggle = { channel -> viewModel.toggleFavorite(channel) },
                                        onOpenPlayerScreen = { channel ->
                                            viewModel.playChannel(channel)
                                            currentRoute = "player"
                                        },
                                        onNextChannel = { viewModel.playNextChannel() },
                                        onPreviousChannel = { viewModel.playPreviousChannel() },
                                        onToggleFullscreen = { viewModel.togglePlayerFullscreen() }
                                    )
                                }
                                "live" -> {
                                    LiveTvScreen(
                                        channels = allChannels,
                                        onChannelSelect = { channel ->
                                            viewModel.playChannel(channel)
                                            currentRoute = "player"
                                        },
                                        onFavoriteToggle = { channel -> viewModel.toggleFavorite(channel) }
                                    )
                                }
                                "favorites" -> {
                                    FavoritesScreen(
                                        favoriteChannels = favoriteChannels,
                                        onChannelSelect = { channel ->
                                            viewModel.playChannel(channel)
                                            currentRoute = "player"
                                        },
                                        onFavoriteToggle = { channel -> viewModel.toggleFavorite(channel) },
                                        onBrowseChannels = { currentRoute = "home" }
                                    )
                                }
                                "search" -> {
                                    SearchScreen(
                                        searchQuery = searchQuery,
                                        filteredChannels = filteredChannels,
                                        selectedCategory = selectedCategory,
                                        onSearchQueryChange = { query -> viewModel.updateSearchQuery(query) },
                                        onCategorySelect = { cat -> viewModel.selectCategory(cat) },
                                        onChannelSelect = { channel ->
                                            viewModel.playChannel(channel)
                                            currentRoute = "player"
                                        },
                                        onFavoriteToggle = { channel -> viewModel.toggleFavorite(channel) }
                                    )
                                }
                                "settings" -> {
                                    SettingsScreen(
                                        settings = settings,
                                        onThemeChange = { mode -> viewModel.updateTheme(mode) },
                                        onAutoLandscapeChange = { enabled -> viewModel.updateAutoLandscape(enabled) },
                                        onRememberLastChannelChange = { enabled -> viewModel.updateRememberLastChannel(enabled) },
                                        onAutoPlayChange = { enabled -> viewModel.updateAutoPlay(enabled) },
                                        onVideoQualityChange = { quality -> viewModel.updateVideoQuality(quality) },
                                        onClearWatchHistory = { viewModel.clearWatchHistory() },
                                        onClearFavorites = { viewModel.clearFavorites() }
                                    )
                                }
                                "player" -> {
                                    activeChannel?.let { channel ->
                                        PlayerScreen(
                                            channel = channel,
                                            allChannels = allChannels,
                                            autoPlay = settings.autoPlay,
                                            videoQuality = settings.videoQuality,
                                            onBackClick = { currentRoute = "home" },
                                            onFavoriteToggle = { ch -> viewModel.toggleFavorite(ch) },
                                            onChannelSelect = { ch -> viewModel.playChannel(ch) },
                                            onNextChannel = { viewModel.playNextChannel() },
                                            onPreviousChannel = { viewModel.playPreviousChannel() },
                                            onToggleFullscreen = { viewModel.togglePlayerFullscreen() }
                                        )
                                    } ?: run {
                                        currentRoute = "home"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
