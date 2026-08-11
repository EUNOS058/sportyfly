package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Live : BottomNavItem("live", "Live TV", Icons.Default.LiveTv)
    object Favorites : BottomNavItem("favorites", "Favorites", Icons.Default.Favorite)
    object Search : BottomNavItem("search", "Search", Icons.Default.Search)
    object Settings : BottomNavItem("settings", "Settings", Icons.Default.Settings)
}

@Composable
fun SportyFlyBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Live,
        BottomNavItem.Favorites,
        BottomNavItem.Search,
        BottomNavItem.Settings
    )

    NavigationBar(
        containerColor = DarkSurface,
        contentColor = Color.White,
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_navigation_bar")
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = RedPrimary,
                    indicatorColor = RedPrimary,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_${item.route}")
            )
        }
    }
}
