package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.ui.components.ChannelCard
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
    favoriteChannels: List<Channel>,
    onChannelSelect: (Channel) -> Unit,
    onFavoriteToggle: (Channel) -> Unit,
    onBrowseChannels: () -> Unit
) {
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
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorites",
                tint = RedPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "My Favorite Channels",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${favoriteChannels.size} channel(s) saved",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (favoriteChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HeartBroken,
                        contentDescription = "No Favorites",
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No favorite channels yet",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Tap the heart icon on any channel card to save it here for quick access.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 32.dp),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onBrowseChannels,
                        colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                    ) {
                        Text("Browse All Channels")
                    }
                }
            }
        } else {
            val chunked = favoriteChannels.chunked(2)
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
