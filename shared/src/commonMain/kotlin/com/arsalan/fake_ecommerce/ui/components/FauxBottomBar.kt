package com.arsalan.fake_ecommerce.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.Tab
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens

/** Short labels for the bottom bar (Notifications shortened to fit four columns). */
private fun Tab.label(): String = when (this) {
    Tab.Home -> "Home"
    Tab.Wishlist -> "Wishlist"
    Tab.Notifications -> "Alerts"
    Tab.Profile -> "Profile"
}

/**
 * Floating, detached bottom navigation bar (Google Photos / iOS gallery style): a rounded,
 * elevated pill that hovers over the content with margins. The selected item animates its colour,
 * scale and a soft highlight pill behind the icon.
 */
@Composable
fun FauxBottomBar(
    selected: Tab,
    onSelect: (Tab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp),
        shape = RoundedCornerShape(30.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 14.dp,
        tonalElevation = 3.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(66.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Tab.entries.forEach { tab ->
                NavItem(
                    tab = tab,
                    selected = tab == selected,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    tab: Tab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reduce = LocalReduceMotion.current
    val active = MaterialTheme.colorScheme.primary
    val inactive = MaterialTheme.colorScheme.onSurfaceVariant
    val iconColor by animateColorAsState(if (selected) active else inactive, label = "navColor")
    val highlight by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f) else androidx.compose.ui.graphics.Color.Transparent,
        label = "navHighlight",
    )
    val scale by animateFloatAsState(
        targetValue = if (selected && !reduce) 1.12f else 1f,
        animationSpec = MotionTokens.bouncy(),
        label = "navScale",
    )
    val interaction = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(highlight)
                .padding(horizontal = 14.dp, vertical = 5.dp),
            contentAlignment = Alignment.Center,
        ) {
            com.arsalan.fake_ecommerce.ui.components.NavGlyph(
                tab = tab,
                color = iconColor,
                filled = selected,
                modifier = Modifier.size(24.dp).graphicsLayer { scaleX = scale; scaleY = scale },
            )
        }
        Text(
            text = tab.label(),
            style = MaterialTheme.typography.labelSmall,
            color = iconColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            maxLines = 1,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}
