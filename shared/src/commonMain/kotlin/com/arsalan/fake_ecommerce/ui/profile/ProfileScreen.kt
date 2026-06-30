package com.arsalan.fake_ecommerce.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.OrderRecord
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.formatUsd
import com.arsalan.fake_ecommerce.ui.theme.FauxGold
import com.arsalan.fake_ecommerce.ui.theme.FauxIndigo
import com.arsalan.fake_ecommerce.ui.theme.FauxMint
import com.arsalan.fake_ecommerce.ui.theme.FauxPink
import com.arsalan.fake_ecommerce.util.nowMillis

private val BottomBarClearance = 112.dp

private data class Reward(val name: String, val threshold: Int, val color: Color)

private val Rewards = listOf(
    Reward("Bronze Saver", 3, FauxGold),
    Reward("Silver Saver", 7, FauxMint),
    Reward("Gold Saver", 14, FauxGold),
    Reward("Platinum Saver", 30, FauxPink),
)

@Composable
fun ProfileScreen(
    state: ShoppingUiState,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = BottomBarClearance),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Avatar(username = state.username)

        OutlinedTextField(
            value = state.username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        StatsRow(
            ordersPlaced = state.ordersPlaced,
            streak = state.streak,
            totalSaved = state.totalSaved,
        )

        SectionCard(title = "Streak rewards") {
            Rewards.forEach { reward ->
                RewardRow(reward = reward, streak = state.streak)
            }
        }

        SectionCard(title = "Daily transactions") {
            DailyTransactions(orders = state.orders)
        }

        SectionCard(title = "History") {
            OrderHistory(orders = state.orders)
        }
    }
}

@Composable
private fun Avatar(username: String) {
    val initials = username.split(' ').filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifBlank { "U" }
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(FauxIndigo, FauxPink))),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                initials,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun StatsRow(ordersPlaced: Int, streak: Int, totalSaved: Double) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard("Items ordered", ordersPlaced.toString(), Modifier.weight(1f))
        StatCard("Streak", streak.toString(), Modifier.weight(1f))
        StatCard("Saved", formatUsd(totalSaved), Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { content() }
        }
    }
}

@Composable
private fun RewardRow(reward: Reward, streak: Int) {
    val unlocked = streak >= reward.threshold
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (unlocked) reward.color else MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                if (unlocked) "*" else "${reward.threshold}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (unlocked) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Column(Modifier.weight(1f)) {
            Text(reward.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(
                if (unlocked) "Unlocked" else "Reach a ${reward.threshold}-session streak",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DailyTransactions(orders: List<OrderRecord>) {
    if (orders.isEmpty()) {
        EmptyHint("No sessions yet - your daily savings will appear here.")
        return
    }
    val today = remember { nowMillis() / 86_400_000L }
    val byDay = orders.groupBy { it.dayIndex }.toList().sortedByDescending { it.first }
    byDay.forEachIndexed { index, (day, dayOrders) ->
        if (index > 0) HorizontalDivider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(relativeDay(today - day), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                Text(
                    "${dayOrders.size} ${if (dayOrders.size == 1) "session" else "sessions"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                "+ ${formatUsd(dayOrders.sumOf { it.amountSaved })}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun OrderHistory(orders: List<OrderRecord>) {
    if (orders.isEmpty()) {
        EmptyHint("No orders yet. Fill a cart and 'check out' to save instead of spend.")
        return
    }
    orders.asReversed().forEachIndexed { index, order ->
        if (index > 0) HorizontalDivider()
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "Order #${order.id} - ${order.itemCount} ${if (order.itemCount == 1) "item" else "items"}",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "saved ${formatUsd(order.amountSaved)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

private fun relativeDay(diff: Long): String = when (diff) {
    0L -> "Today"
    1L -> "Yesterday"
    else -> "$diff days ago"
}
