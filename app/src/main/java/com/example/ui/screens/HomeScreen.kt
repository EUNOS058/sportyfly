package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.data.sample.SampleChannels
import com.example.ui.components.ChannelCard
import com.example.ui.components.ExoVideoPlayer
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    channels: List<Channel>,
    recentlyWatched: List<Channel>,
    activeChannel: Channel?,
    selectedCategory: String,
    searchQuery: String,
    autoPlay: Boolean,
    videoQuality: String,
    onCategorySelect: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onChannelSelect: (Channel) -> Unit,
    onFavoriteToggle: (Channel) -> Unit,
    onOpenPlayerScreen: (Channel) -> Unit,
    onNextChannel: () -> Unit,
    onPreviousChannel: () -> Unit,
    onToggleFullscreen: () -> Unit
) {
    val categories = SampleChannels.CATEGORIES
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp)
    ) {
        // 1. Featured Live Player Section at Top
        if (activeChannel != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    ExoVideoPlayer(
                        streamUrl = activeChannel.streamUrl,
                        channelName = activeChannel.name,
                        isLive = activeChannel.isLive,
                        autoPlay = autoPlay,
                        qualityPreference = videoQuality,
                        onPreviousChannel = onPreviousChannel,
                        onNextChannel = onNextChannel,
                        onToggleFullscreen = onToggleFullscreen,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Active Channel Info Bar under Player
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeChannel.name,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (activeChannel.isLive) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = LiveBadgeRed,
                                    shape = RoundedCornerShape(4.dp)
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
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${activeChannel.category} • ${activeChannel.country} • ${activeChannel.language}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { onFavoriteToggle(activeChannel) },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DarkSurfaceVariant)
                        ) {
                            Icon(
                                imageVector = if (activeChannel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (activeChannel.isFavorite) RedPrimary else Color.White
                            )
                        }

                        Button(
                            onClick = { onOpenPlayerScreen(activeChannel) },
                            colors = ButtonDefaults.buttonColors(containerColor = RedPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("open_fullscreen_player_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Expand",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Expand Player")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Real-Time Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Search channels, movies, sports...", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = RedPrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear Search", tint = TextSecondary)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RedPrimary,
                unfocusedBorderColor = CardBorderColor,
                focusedContainerColor = DarkSurfaceVariant,
                unfocusedContainerColor = DarkSurfaceVariant,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("home_search_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Recently Watched Carousel (Local persistence)
        if (recentlyWatched.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Watched",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${recentlyWatched.size} item(s)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recentlyWatched) { channel ->
                        ChannelCard(
                            channel = channel,
                            onChannelClick = onChannelSelect,
                            onFavoriteToggle = onFavoriteToggle,
                            modifier = Modifier.width(170.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // 4. Categories Horizontal Filter Chips
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Browse Categories",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory.equals(category, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelect(category) },
                        label = {
                            Text(
                                text = category,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RedPrimary,
                            containerColor = DarkSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) RedPrimary else CardBorderColor,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("category_chip_$category")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Channels Grid Section
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedCategory == "All") "All Channels" else "$selectedCategory Channels",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${channels.size} channel(s)",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (channels.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.TvOff,
                            contentDescription = "No channels",
                            tint = TextSecondary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No channels found",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search query or category filter.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                // Non-nested vertical grid simulation inside scrollable column
                val chunkedChannels = channels.chunked(2)
                chunkedChannels.forEach { rowChannels ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowChannels.forEach { channel ->
                            ChannelCard(
                                channel = channel,
                                onChannelClick = onChannelSelect,
                                onFavoriteToggle = onFavoriteToggle,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowChannels.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
