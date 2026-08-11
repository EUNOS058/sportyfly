package com.example.ui.components

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TranslucentGlass
import kotlinx.coroutines.delay

@OptIn(UnstableApi::class)
@Composable
fun ExoVideoPlayer(
    streamUrl: String,
    channelName: String,
    isLive: Boolean,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
    isFullscreen: Boolean = false,
    onToggleFullscreen: (() -> Unit)? = null,
    onPreviousChannel: (() -> Unit)? = null,
    onNextChannel: (() -> Unit)? = null,
    onBackClick: (() -> Unit)? = null,
    qualityPreference: String = "Auto"
) {
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(autoPlay) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var playerStatusText by remember { mutableStateOf("Loading...") }
    var isMuted by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var currentQuality by remember { mutableStateOf(qualityPreference) }

    // Controls autohide timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4000)
            showControls = false
        }
    }

    val exoPlayer = remember(context, streamUrl) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("SportyFly-AndroidPlayer/1.0 (Linux; Android)")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(httpDataSourceFactory)

        val uri = Uri.parse(streamUrl)
        val mediaItemBuilder = MediaItem.Builder().setUri(uri)
        if (streamUrl.contains(".m3u8", ignoreCase = true)) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        } else if (streamUrl.contains(".mpd", ignoreCase = true)) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_MPD)
        } else if (streamUrl.contains(".mp4", ignoreCase = true)) {
            mediaItemBuilder.setMimeType(MimeTypes.VIDEO_MP4)
        }

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(mediaSourceFactory)
            .build().apply {
                setMediaItem(mediaItemBuilder.build())
                prepare()
                playWhenReady = autoPlay
            }
    }

    // Attach listener
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isLoading = true
                        hasError = false
                        playerStatusText = "Buffering"
                    }
                    Player.STATE_READY -> {
                        isLoading = false
                        hasError = false
                        playerStatusText = if (exoPlayer.isPlaying) "Playing" else "Paused"
                    }
                    Player.STATE_ENDED -> {
                        isLoading = false
                        isPlaying = false
                        playerStatusText = "Paused"
                    }
                    Player.STATE_IDLE -> {
                        if (!hasError) {
                            playerStatusText = "Loading..."
                        }
                    }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (!hasError && !isLoading) {
                    playerStatusText = if (playing) "Playing" else "Paused"
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isLoading = false
                hasError = true
                val codeName = error.errorCodeName
                val causeMsg = error.cause?.message ?: error.message ?: "Network or source unavailable"
                val isUnavailable = error.errorCode == PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ||
                                    error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ||
                                    error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ||
                                    error.errorCode == PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED

                if (isUnavailable) {
                    playerStatusText = "Stream unavailable"
                    errorMessage = "Stream unavailable ($codeName: $causeMsg)"
                } else {
                    playerStatusText = "Playback error"
                    errorMessage = "Playback error: $causeMsg [$codeName]"
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
            .testTag("player_container")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControls = !showControls
            }
    ) {
        // ExoPlayer AndroidView
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false // Custom Compose controls overlay
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    keepScreenOn = true
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            update = { playerView ->
                if (playerView.player != exoPlayer) {
                    playerView.player = exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .testTag("player_view")
        )

        // Loading Spinner
        if (isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = RedPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = playerStatusText,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Error State Overlay
        if (hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xEE0B0F19)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = RedPrimary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = playerStatusText,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        color = Color.LightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            hasError = false
                            isLoading = true
                            playerStatusText = "Loading..."
                            exoPlayer.prepare()
                            exoPlayer.play()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("retry_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Retry",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry Playback")
                    }
                }
            }
        }

        // Custom Overlay Controls
        AnimatedVisibility(
            visible = showControls || !isPlaying || hasError,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showControls = !showControls
                    }
            ) {
                // Top Player Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(TranslucentGlass)
                                .testTag("player_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = channelName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (isLive) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(LiveBadgeRed)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE BROADCAST",
                                    color = LiveBadgeRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Quality Selector Button
                    Surface(
                        onClick = { showQualityDialog = true },
                        color = TranslucentGlass,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.HighQuality,
                                contentDescription = "Quality",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentQuality,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Mute/Unmute
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            exoPlayer.volume = if (isMuted) 0f else 1f
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(TranslucentGlass)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Volume",
                            tint = Color.White
                        )
                    }
                }

                // Center Play / Previous / Next / Reload Controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (onPreviousChannel != null) {
                        IconButton(
                            onClick = onPreviousChannel,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TranslucentGlass)
                                .testTag("prev_channel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous Channel",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Main Play/Pause Button
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(RedPrimary)
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    if (onNextChannel != null) {
                        IconButton(
                            onClick = onNextChannel,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(TranslucentGlass)
                                .testTag("next_channel_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next Channel",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Reload Button
                    IconButton(
                        onClick = {
                            exoPlayer.prepare()
                            exoPlayer.play()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(TranslucentGlass)
                            .testTag("reload_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reload",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Bottom Controls Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when (playerStatusText) {
                                        "Playing" -> Color(0xFF10B981)
                                        "Buffering", "Loading..." -> Color(0xFFF59E0B)
                                        "Paused" -> Color.Gray
                                        else -> RedPrimary
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = playerStatusText,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("player_status_badge")
                        )
                    }

                    // Fullscreen Toggle Button
                    if (onToggleFullscreen != null) {
                        IconButton(
                            onClick = onToggleFullscreen,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(TranslucentGlass)
                                .testTag("fullscreen_button")
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Quality Dialog Modal
        if (showQualityDialog) {
            AlertDialog(
                onDismissRequest = { showQualityDialog = false },
                title = { Text("Select Stream Quality", color = Color.White) },
                text = {
                    Column {
                        listOf("Auto", "1080p Full HD", "720p HD", "480p SD").forEach { quality ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentQuality = quality.split(" ").first()
                                        showQualityDialog = false
                                    }
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = currentQuality == quality.split(" ").first(),
                                    onClick = {
                                        currentQuality = quality.split(" ").first()
                                        showQualityDialog = false
                                    },
                                    colors = RadioButtonDefaults.colors(selectedColor = RedPrimary)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(quality, color = Color.White)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showQualityDialog = false }) {
                        Text("Close", color = RedPrimary)
                    }
                },
                containerColor = Color(0xFF161F33)
            )
        }
    }
}
