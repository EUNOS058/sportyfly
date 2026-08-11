package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
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
import com.example.data.sample.SampleChannels
import com.example.ui.components.ChannelCard
import com.example.ui.theme.CardBorderColor
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.RedPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SearchScreen(
    searchQuery: String,
    filteredChannels: List<Channel>,
    selectedCategory: String,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onChannelSelect: (Channel) -> Unit,
    onFavoriteToggle: (Channel) -> Unit
) {
    val categories = SampleChannels.CATEGORIES
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Search SportyFly",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Search channels by name, category, language or country",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Large Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Type channel or movie name...", color = TextSecondary) },
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
                .testTag("search_screen_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory.equals(category, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category, color = if (isSelected) Color.White else TextSecondary) },
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
                    modifier = Modifier.testTag("search_filter_$category")
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Results (${filteredChannels.size})",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredChannels.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = "No Results",
                        tint = TextSecondary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No results found",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Try searching with different keywords like 'Sports' or 'Bangla'.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            val chunked = filteredChannels.chunked(2)
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
