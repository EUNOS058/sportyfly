package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LiveTvScreen(
    channels: List<Channel>,
    onChannelSelect: (Channel) -> Unit,
    onFavoriteToggle: (Channel) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All Live") }
    val filters = listOf("All Live", "Sports", "Bangla", "News", "Kids", "Entertainment")

    val liveChannels = channels.filter { channel ->
        channel.isLive && (selectedFilter == "All Live" || channel.category.equals(selectedFilter, ignoreCase = true))
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LiveTv,
                contentDescription = "Live TV",
                tint = RedPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Live TV Broadcasts",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Real-time sports, news, and live channels",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, color = if (isSelected) Color.White else TextSecondary) },
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
                    modifier = Modifier.testTag("live_filter_$filter")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (liveChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = "Empty",
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Live streams available in this category",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            val chunked = liveChannels.chunked(2)
            chunked.forEach { rowChannels ->
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
