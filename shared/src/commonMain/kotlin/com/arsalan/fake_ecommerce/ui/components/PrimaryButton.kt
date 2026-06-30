package com.arsalan.fake_ecommerce.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens

/**
 * Tactile primary button: compresses on press with a spring and rebounds on release. Honours
 * reduce-motion by skipping the scale animation.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val reduce = LocalReduceMotion.current
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reduce) 0.94f else 1f,
        animationSpec = MotionTokens.bouncy(),
        label = "buttonPressScale",
    )
    Button(
        onClick = onClick,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        enabled = enabled,
        interactionSource = interaction,
        colors = ButtonDefaults.buttonColors(),
    ) {
        Text(text)
    }
}
