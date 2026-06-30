package com.arsalan.fake_ecommerce.ui.reward

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.LottieAssets
import com.arsalan.fake_ecommerce.ui.components.LottieBox
import com.arsalan.fake_ecommerce.ui.components.PrimaryButton
import com.arsalan.fake_ecommerce.ui.components.formatUsd
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens

/**
 * The emotional payoff. A full-screen Lottie celebration plays once while the saved amount counts
 * up and the streak badge pops. Everything reframes the moment around what the user kept.
 */
@Composable
fun RewardScreen(
    state: ShoppingUiState,
    onNewSession: () -> Unit,
) {
    val reduce = LocalReduceMotion.current
    val saved = state.lastOrderSaved.toFloat()

    val counter = remember { Animatable(if (reduce) saved else 0f) }
    LaunchedEffect(saved) {
        if (!reduce) {
            counter.snapTo(0f)
            counter.animateTo(saved, tween(durationMillis = 1200))
        } else {
            counter.snapTo(saved)
        }
    }

    val streakScale = remember { Animatable(if (reduce) 1f else 0.4f) }
    LaunchedEffect(Unit) {
        if (!reduce) streakScale.animateTo(1f, MotionTokens.bouncy())
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        LottieBox(
            assetPath = LottieAssets.Celebration,
            iterations = 1,
            modifier = Modifier.fillMaxSize(),
            fallback = {},
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "You just saved",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = formatUsd(counter.value.toDouble()),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                "and avoided a compulsive purchase.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.graphicsLayer { scaleX = streakScale.value; scaleY = streakScale.value },
            ) {
                Column(
                    Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "${state.streak}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                    Text("session streak", style = MaterialTheme.typography.labelMedium)
                }
            }

            Text(
                "Total kept: ${formatUsd(state.totalSaved)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            PrimaryButton(
                text = "Keep the streak going",
                onClick = onNewSession,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
