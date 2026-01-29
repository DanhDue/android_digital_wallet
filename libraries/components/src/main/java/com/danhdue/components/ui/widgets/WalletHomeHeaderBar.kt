package com.danhdue.components.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.danhdue.components.ui.theme.TrueBlue
import com.danhdue.components.ui.theme.WalletCardGradient
import com.danhdue.libraries.components.R

@Composable

fun WalletHomeHeaderBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // Avatar
        Box(
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(
                modifier = Modifier.alpha(0f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = TrueBlue, // Matching the blue outline style in image
                    modifier = Modifier.size(28.dp),
                )

                Icon(
                    painter = painterResource(id = R.drawable.ic_bitcoin_card),
                    contentDescription = "Card",
                    tint = TrueBlue, // Assuming original colors for the card icon
                    modifier = Modifier.size(28.dp),
                )
            }

            Image(
                painter = painterResource(id = R.drawable.avatar_13),
                contentDescription = "User Avatar",
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(40.dp)
                        .clip(CircleShape),
            )
        }
        // Network Selector Pill
        Box(
            modifier =
                Modifier
                    .background(
                        brush = WalletCardGradient,
                        shape = RoundedCornerShape(50),
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Placeholder for Network Icon since it wasn't explicitly provided and search failed.
                // Using a standard icon or omitting if not available.
                // Assuming R.drawable.ic_search is available, we use that for search.
                // Ideally we use a computer/monitor icon.
                Icon(
                    imageVector = Icons.Default.Monitor, // Fallback to Material Icon
                    contentDescription = "Network Icon",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )

                Text(
                    text = "All Networks",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown Arrow",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        // Action Buttons
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search",
                tint = TrueBlue, // Matching the blue outline style in image
                modifier = Modifier.size(28.dp),
            )

            Icon(
                painter = painterResource(id = R.drawable.ic_bitcoin_card),
                contentDescription = "Card",
                tint = TrueBlue, // Assuming original colors for the card icon
                modifier = Modifier.size(28.dp),
            )
        }
    }
}


@Preview
@Composable
fun WalletHomeHeaderBarPreview() {
    WalletHomeHeaderBar()
}
