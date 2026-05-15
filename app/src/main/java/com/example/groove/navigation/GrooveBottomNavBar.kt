package com.example.groove.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GrooveBottomNavBar(
    tabs: List<BottomNavTab>,
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEach { tab ->
                NavItem(
                    tab = tab,
                    selected = tab.route == selectedTab.route,
                    onTabSelected = { onTabSelected(tab) },
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    tab: BottomNavTab,
    selected: Boolean,
    onTabSelected: () -> Unit,
) {
    val pillBg = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val activeIconColor = MaterialTheme.colorScheme.primary
    val inactiveIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)

    val iconTint by animateColorAsState(
        targetValue = if (selected) activeIconColor else inactiveIconColor,
        animationSpec = tween(200),
        label = "navIconTint",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .then(if (selected) Modifier.background(pillBg) else Modifier)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTabSelected,
            )
            .padding(
                horizontal = if (selected) 14.dp else 14.dp,
                vertical = 8.dp,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
            if (selected) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = tab.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = activeIconColor,
                )
            }
        }
    }
}
