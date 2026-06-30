package com.arsalan.fake_ecommerce.ui.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Single source of truth for motion (DRY): every screen and component pulls its spring/duration
 * from here so the whole app feels coherent. Springs are preferred for anything the user touches -
 * they are natural and interruptible; tweens are used for deterministic, timed transitions.
 */
object MotionTokens {
    const val DurationShort = 180
    const val DurationMedium = 320
    const val DurationLong = 480

    /** Lively overshoot for tactile presses, badge pops and reward moments. */
    fun <T> bouncy(): FiniteAnimationSpec<T> =
        spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow)

    /** Quick, low-overshoot response for selection and small state changes. */
    fun <T> snappy(): FiniteAnimationSpec<T> =
        spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMedium)

    /** Smooth, settled motion for the fly-to-cart path and content fades. */
    fun <T> smooth(durationMillis: Int = DurationMedium): FiniteAnimationSpec<T> =
        tween(durationMillis = durationMillis)
}
