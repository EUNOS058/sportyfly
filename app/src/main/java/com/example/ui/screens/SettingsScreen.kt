package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.UserSettings
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    settings: UserSettings,
    onThemeChange: (String) -> Unit,
    onAutoLandscapeChange: (Boolean) -> Unit,
    onRememberLastChannelChange: (Boolean) -> Unit,
    onAutoPlayChange: (Boolean) -> Unit,
    onVideoQualityChange: (String) -> Unit,
    onClearWatchHistory: () -> Unit,
    onClearFavorites: () -> Unit
) {
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showClearFavoritesDialog by remember { mutableStateOf(false) }
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
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = RedPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "App Settings",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Playback, theme & data management",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: Appearance & Theme
        SettingsSectionHeader(title = "Appearance & Theme")

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "Theme Mode", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Dark", "Light", "System").forEach { mode ->
                        val isSelected = settings.themeMode == mode
                        Button(
                            onClick = { onThemeChange(mode) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) RedPrimary else Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("theme_btn_$mode")
                        ) {
                            Text(mode, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 2: Player & Streaming
        SettingsSectionHeader(title = "Video Player & Playback")

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingsSwitchRow(
                    title = "Auto-Play Streams",
                    subtitle = "Automatically start playing stream when channel is selected",
                    icon = Icons.Default.PlayCircle,
                    checked = settings.autoPlay,
                    onCheckedChange = onAutoPlayChange,
                    testTag = "switch_autoplay"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderColor)

                SettingsSwitchRow(
                    title = "Auto Landscape Fullscreen",
                    subtitle = "Switch to landscape view during fullscreen video playback",
                    icon = Icons.Default.ScreenRotation,
                    checked = settings.autoLandscapeFullscreen,
                    onCheckedChange = onAutoLandscapeChange,
                    testTag = "switch_auto_landscape"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderColor)

                SettingsSwitchRow(
                    title = "Remember Last Channel",
                    subtitle = "Resume playback of your last active stream on app launch",
                    icon = Icons.Default.History,
                    checked = settings.rememberLastChannel,
                    onCheckedChange = onRememberLastChannelChange,
                    testTag = "switch_remember_channel"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderColor)

                // Video Quality Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HighQuality, contentDescription = "Quality", tint = RedPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Default Stream Quality", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text("Preferred resolution for video streams", color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            listOf("Auto", "1080p", "720p", "480p").forEach { q ->
                                Text(
                                    text = q,
                                    color = if (settings.videoQuality == q) RedPrimary else TextSecondary,
                                    fontWeight = if (settings.videoQuality == q) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp,
                                    modifier = Modifier
                                        .clickable { onVideoQualityChange(q) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 3: Data Management
        SettingsSectionHeader(title = "Data & Local Persistence")

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SettingsActionRow(
                    title = "Clear Watch History",
                    subtitle = "Remove all recently watched channel logs",
                    icon = Icons.Default.DeleteSweep,
                    onClick = { showClearHistoryDialog = true },
                    testTag = "clear_history_button"
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = CardBorderColor)

                SettingsActionRow(
                    title = "Clear Favorites",
                    subtitle = "Remove all bookmarked favorite channels",
                    icon = Icons.Default.HeartBroken,
                    onClick = { showClearFavoritesDialog = true },
                    testTag = "clear_favorites_button"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 4: About & Version
        SettingsSectionHeader(title = "About SportyFly")

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = "About", tint = RedPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("SportyFly OTT Live TV", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SportyFly is a high-performance modern Android live TV & sports streaming application powered by ExoPlayer/Media3 and Jetpack Compose.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("Version: 1.0.0 (Build 100)", color = RedPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    // Confirmation Dialogs
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear Watch History?", color = Color.White) },
            text = { Text("Are you sure you want to clear your recently watched channels history?", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearWatchHistory()
                        showClearHistoryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF161F33)
        )
    }

    if (showClearFavoritesDialog) {
        AlertDialog(
            onDismissRequest = { showClearFavoritesDialog = false },
            title = { Text("Clear All Favorites?", color = Color.White) },
            text = { Text("Are you sure you want to remove all channels from your favorites list?", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearFavorites()
                        showClearFavoritesDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedPrimary)
                ) {
                    Text("Clear Favorites")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearFavoritesDialog = false }) {
                    Text("Cancel", color = Color.White)
                }
            },
            containerColor = Color(0xFF161F33)
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = RedPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = RedPrimary,
                uncheckedThumbColor = TextSecondary,
                uncheckedTrackColor = Color(0xFF0F172A)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun SettingsActionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = RedPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
            }
        }
        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Action", tint = TextSecondary)
    }
}
