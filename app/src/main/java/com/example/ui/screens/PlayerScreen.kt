package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.components.ExoVideoPlayer
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PlayerScreen(
    channel: Channel,
    allChannels: List<Channel>,
    autoPlay: Boolean,
    videoQuality: String,
    onBackClick: () -> Unit,
    onFavoriteToggle: (Channel) -> Unit,
    onChannelSelect: (Channel) -> Unit,
    onNextChannel: () -> Unit,
    onPreviousChannel: () -> Unit,
    onToggleFullscreen: () -> Unit
) {
    val scrollState = rememberScrollState()
    val relatedChannels = allChannels.filter { it.id != channel.id }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavyBackground)
            .verticalScroll(scrollState)
    ) {
        // Player Container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            ExoVideoPlayer(
                streamUrl = channel.streamUrl,
                channelName = channel.name,
                isLive = channel.isLive,
                autoPlay = autoPlay,
                qualityPreference = videoQuality,
                onBackClick = onBackClick,
                onPreviousChannel = onPreviousChannel,
                onNextChannel = onNextChannel,
                onToggleFullscreen = onToggleFullscreen,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Channel Detail Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = channel.name,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = RedPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = channel.category,
                                color = RedPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        if (channel.isLive) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = LiveBadgeRed,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(
                        onClick = { onFavoriteToggle(channel) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DarkSurfaceVariant)
                            .testTag("player_favorite_toggle")
                    ) {
                        Icon(
                            imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (channel.isFavorite) RedPrimary else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Country: ${channel.country}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Language: ${channel.language}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                    if (channel.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = channel.description,
                            color = Color.LightGray,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Up Next / Related Channels
            Text(
                text = "Up Next / More Channels",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(relatedChannels) { item ->
                    ChannelCard(
                        channel = item,
                        onChannelClick = onChannelSelect,
                        onFavoriteToggle = onFavoriteToggle,
                        modifier = Modifier.width(170.dp)
                    )
                }
            }
        }
    }
}
