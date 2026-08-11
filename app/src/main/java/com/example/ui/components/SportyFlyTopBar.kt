package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.DarkNavyBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SportyFlyTopBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    isCompact: Boolean = false
) {
    val navTabs = listOf("Home", "Live TV", "Favorites", "Search", "Settings")

    Surface(
        color = DarkSurface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DarkSurface, DarkNavyBackground)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Logo Branding
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onTabSelected("Home") }
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(RedPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_sportyfly_icon),
                            contentDescription = "SportyFly Logo",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SPORTY",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "FLY",
                        color = RedPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Header Action Icons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier.testTag("top_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = {},
                        modifier = Modifier.testTag("top_notification_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = onProfileClick,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(RedPrimary.copy(alpha = 0.2f))
                            .testTag("top_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = RedPrimary
                        )
                    }
                }
            }

            // Desktop / Wide Navigation Tabs Row (Hidden on very compact screens)
            if (!isCompact) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navTabs.forEach { tab ->
                        val isSelected = selectedTab.equals(tab, ignoreCase = true)
                        Text(
                            text = tab,
                            color = if (isSelected) RedPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier
                                .clickable { onTabSelected(tab) }
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
