package com.arsalan.fake_ecommerce.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * When true, animations should be replaced with quick fades / static frames for accessibility.
 * Defaulting to false; a platform can override it by reading the OS reduce-motion setting and
 * providing it at the root.
 */
val LocalReduceMotion = staticCompositionLocalOf { false }

private val FauxColors = darkColorScheme(
    primary = FauxIndigo,
    onPrimary = FauxOnDark,
    primaryContainer = FauxIndigoDark,
    onPrimaryContainer = FauxOnDark,
    secondary = FauxGold,
    onSecondary = FauxInk,
    tertiary = FauxMint,
    background = FauxInk,
    onBackground = FauxOnDark,
    surface = FauxSurface,
    onSurface = FauxOnDark,
    surfaceVariant = FauxSurfaceVariant,
    onSurfaceVariant = FauxOnDarkMuted,
    error = FauxPink,
)

@Composable
fun FauxCartTheme(
    reduceMotion: Boolean = false,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalReduceMotion provides reduceMotion) {
        MaterialTheme(
            colorScheme = FauxColors,
            content = content,
        )
    }
}
