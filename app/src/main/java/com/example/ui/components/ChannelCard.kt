package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Channel
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.LiveBadgeRed
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TranslucentGlass

@Composable
fun ChannelCard(
    channel: Channel,
    onChannelClick: (Channel) -> Unit,
    onFavoriteToggle: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isHovered by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.03f else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "scale"
    )

    val heartScale by animateFloatAsState(
        targetValue = if (channel.isFavorite) 1.2f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    val favoriteColor by animateColorAsState(
        targetValue = if (channel.isFavorite) RedPrimary else Color.White,
        label = "favoriteColor"
    )

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        modifier = modifier
            .scale(scale)
            .border(1.dp, CardBorderColor, RoundedCornerShape(14.dp))
            .clickable { onChannelClick(channel) }
            .testTag("channel_card_${channel.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Channel Thumbnail / Logo area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(Color(0xFF0F172A)),
                contentAlignment = Alignment.Center
            ) {
                // Background Poster or Logo
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(channel.posterUrl.ifEmpty { channel.logoUrl })
                        .crossfade(true)
                        .build(),
                    contentDescription = channel.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                            )
                        )
                )

                // LIVE badge on top-left
                if (channel.isLive) {
                    Surface(
                        color = LiveBadgeRed,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LIVE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Favorite Heart Button on top-right
                IconButton(
                    onClick = { onFavoriteToggle(channel) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(TranslucentGlass)
                        .testTag("fav_button_${channel.id}")
                ) {
                    Icon(
                        imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = favoriteColor,
                        modifier = Modifier
                            .size(18.dp)
                            .scale(heartScale)
                    )
                }

                // Center Play Icon Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(RedPrimary.copy(alpha = 0.9f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Channel Details Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = channel.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = channel.category,
                        color = RedPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = channel.language,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
