package com.arsalan.fake_ecommerce.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.formatUsd
import com.arsalan.fake_ecommerce.ui.theme.FauxGold
import com.arsalan.fake_ecommerce.ui.theme.FauxMint
import com.arsalan.fake_ecommerce.ui.theme.FauxPink

private val BottomBarClearance = 112.dp

private data class Notice(val title: String, val body: String, val accent: Color)

@Composable
fun NotificationsScreen(state: ShoppingUiState) {
    val notices = buildList {
        add(
            Notice(
                "Welcome to FauxCart",
                "The only store where checkout leaves you richer. Browse all you like.",
                MaterialTheme.colorScheme.primary,
            ),
        )
        if (state.streak > 0) {
            add(Notice("Streak alive", "${state.streak} sessions saved in a row. Don't break the chain.", FauxGold))
        }
        if (state.totalSaved > 0) {
            add(Notice("Money kept", "You've avoided ${formatUsd(state.totalSaved)} in impulse buys so far.", FauxMint))
        }
        state.orders.takeLast(3).reversed().forEach { order ->
            add(Notice("Near miss avoided", "Order #${order.id}: you walked away from ${formatUsd(order.amountSaved)}.", FauxMint))
        }
        if (state.wishlistIds.isNotEmpty()) {
            add(Notice("Your wishlist is waiting", "${state.wishlistIds.size} saved items still cost nothing to admire.", FauxPink))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = BottomBarClearance),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(notices) { notice -> NoticeRow(notice) }
    }
}

@Composable
private fun NoticeRow(notice: Notice) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                modifier = Modifier.size(12.dp).clip(CircleShape).background(notice.accent),
            )
            Column(Modifier.fillMaxWidth()) {
                Text(notice.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    notice.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
