package com.arsalan.fake_ecommerce.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
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
import com.arsalan.fake_ecommerce.ui.theme.FauxInk
import com.arsalan.fake_ecommerce.ui.theme.FauxOnDark
import com.arsalan.fake_ecommerce.ui.theme.FauxOnDarkMuted
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens
import fakeecommerce.shared.generated.resources.Res
import fakeecommerce.shared.generated.resources.fauxcart_logo
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

/**
 * Launch splash: the brand mark springs and fades in over the dark brand background, then the app
 * crossfades in. Honours reduce-motion by showing the logo immediately.
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val reduce = LocalReduceMotion.current
    val scale = remember { Animatable(if (reduce) 1f else 0.6f) }
    val alpha = remember { Animatable(if (reduce) 1f else 0f) }

    LaunchedEffect(Unit) {
        if (!reduce) {
            launch { alpha.animateTo(1f, tween(durationMillis = 450)) }
            scale.animateTo(1f, MotionTokens.bouncy())
        }
    }

    Box(
        modifier = modifier.fillMaxSize().background(FauxInk),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.fauxcart_logo),
                contentDescription = "FauxCart",
                modifier = Modifier
                    .size(168.dp)
                    .graphicsLayer { scaleX = scale.value; scaleY = scale.value; this.alpha = alpha.value },
            )
            Text(
                "FauxCart",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = FauxOnDark,
                modifier = Modifier.graphicsLayer { this.alpha = alpha.value },
            )
            Text(
                "Shop the feeling. Keep the money.",
                style = MaterialTheme.typography.bodyMedium,
                color = FauxOnDarkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.graphicsLayer { this.alpha = alpha.value },
            )
        }
    }
}
