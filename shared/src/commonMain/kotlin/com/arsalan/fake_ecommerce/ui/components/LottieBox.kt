package com.arsalan.fake_ecommerce.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import fakeecommerce.shared.generated.resources.Res
import io.github.alexzhirkevich.compottie.Compottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Renders a Lottie animation from a bundled JSON asset under the composeResources files directory,
 * using Compottie's pure-Kotlin renderer (Android, iOS, web/Wasm).
 *
 * Two safety nets keep the app robust:
 *  - reduce-motion: shows the [fallback] instead of animating;
 *  - missing/invalid asset or composition still loading: shows the [fallback].
 *
 * This lets set-piece animations be developed and shipped against placeholder Lottie files and
 * swapped for bespoke After Effects exports later, without touching call sites.
 *
 * NOTE: written against the Compottie 2.x API. If the resolved Compottie version exposes a slightly
 * different surface, only this file needs adjusting.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun LottieBox(
    assetPath: String,
    modifier: Modifier = Modifier,
    iterations: Int = Compottie.IterateForever,
    fallback: @Composable () -> Unit = {},
) {
    if (LocalReduceMotion.current) {
        fallback()
        return
    }

    var json by remember(assetPath) { mutableStateOf<String?>(null) }
    LaunchedEffect(assetPath) {
        json = runCatching { Res.readBytes(assetPath).decodeToString() }.getOrNull()
    }

    val data = json ?: run { fallback(); return }

    val composition by rememberLottieComposition { LottieCompositionSpec.JsonString(data) }
    val progress by animateLottieCompositionAsState(composition, iterations = iterations)

    val comp = composition ?: run { fallback(); return }

    Image(
        painter = rememberLottiePainter(composition = comp, progress = { progress }),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}

/** Asset paths for the bundled placeholder Lottie files. */
object LottieAssets {
    const val Celebration = "files/celebration.json"
    const val Processing = "files/processing.json"
    const val Delivery = "files/delivery.json"
}
