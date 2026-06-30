package com.arsalan.fake_ecommerce.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.Tab
import fakeecommerce.shared.generated.resources.Res
import fakeecommerce.shared.generated.resources.fauxcart_logo
import org.jetbrains.compose.resources.painterResource

/**
 * Lightweight, dependency-free line icons drawn with Canvas (the project intentionally avoids the
 * Material icon font). Each is a simple geometric path; pass [filled] where a solid state is wanted.
 */

private fun DrawScope.strokePath(build: Path.() -> Unit, color: Color, filled: Boolean) {
    val p = Path().apply(build)
    val w = size.minDimension * 0.085f
    if (filled) drawPath(p, color) else drawPath(p, color, style = Stroke(width = w, cap = StrokeCap.Round, join = StrokeJoin.Round))
}

@Composable
fun NavGlyph(tab: Tab, color: Color, modifier: Modifier = Modifier, filled: Boolean = false) {
    when (tab) {
        Tab.Home -> HomeGlyph(color, modifier, filled)
        Tab.Wishlist -> HeartGlyph(color, modifier, filled)
        Tab.Notifications -> BellGlyph(color, modifier, filled)
        Tab.Profile -> PersonGlyph(color, modifier, filled)
    }
}

@Composable
fun HomeGlyph(color: Color, modifier: Modifier = Modifier, filled: Boolean = false) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        strokePath({
            moveTo(0.16f * w, 0.46f * h)
            lineTo(0.5f * w, 0.14f * h)
            lineTo(0.84f * w, 0.46f * h)
            moveTo(0.24f * w, 0.42f * h)
            lineTo(0.24f * w, 0.84f * h)
            lineTo(0.76f * w, 0.84f * h)
            lineTo(0.76f * w, 0.42f * h)
            moveTo(0.43f * w, 0.84f * h)
            lineTo(0.43f * w, 0.62f * h)
            lineTo(0.57f * w, 0.62f * h)
            lineTo(0.57f * w, 0.84f * h)
        }, color, filled = false)
    }
}

@Composable
fun HeartGlyph(color: Color, modifier: Modifier = Modifier, filled: Boolean = false) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        strokePath({
            moveTo(0.5f * w, 0.82f * h)
            cubicTo(0.06f * w, 0.54f * h, 0.16f * w, 0.16f * h, 0.5f * w, 0.34f * h)
            cubicTo(0.84f * w, 0.16f * h, 0.94f * w, 0.54f * h, 0.5f * w, 0.82f * h)
            close()
        }, color, filled)
    }
}

@Composable
fun BellGlyph(color: Color, modifier: Modifier = Modifier, filled: Boolean = false) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        strokePath({
            moveTo(0.28f * w, 0.66f * h)
            quadraticTo(0.28f * w, 0.30f * h, 0.5f * w, 0.26f * h)
            quadraticTo(0.72f * w, 0.30f * h, 0.72f * w, 0.66f * h)
            close()
            moveTo(0.2f * w, 0.66f * h)
            lineTo(0.8f * w, 0.66f * h)
        }, color, filled)
        // clapper
        drawCircle(color, radius = size.minDimension * 0.06f, center = Offset(0.5f * w, 0.78f * h))
    }
}

@Composable
fun PersonGlyph(color: Color, modifier: Modifier = Modifier, filled: Boolean = false) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        val sw = size.minDimension * 0.085f
        if (filled) {
            drawCircle(color, radius = 0.17f * w, center = Offset(0.5f * w, 0.33f * h))
            val body = Path().apply {
                moveTo(0.2f * w, 0.84f * h)
                quadraticTo(0.5f * w, 0.54f * h, 0.8f * w, 0.84f * h)
                close()
            }
            drawPath(body, color)
        } else {
            drawCircle(color, radius = 0.17f * w, center = Offset(0.5f * w, 0.33f * h), style = Stroke(width = sw))
            val body = Path().apply {
                moveTo(0.22f * w, 0.84f * h)
                quadraticTo(0.5f * w, 0.55f * h, 0.78f * w, 0.84f * h)
            }
            drawPath(body, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
        }
    }
}

/** Shopping-bag glyph reused for the cart action and the brand mark. */
@Composable
fun BagGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        val sw = size.minDimension * 0.085f
        val body = Path().apply {
            moveTo(0.26f * w, 0.36f * h)
            lineTo(0.74f * w, 0.36f * h)
            lineTo(0.8f * w, 0.84f * h)
            lineTo(0.2f * w, 0.84f * h)
            close()
        }
        drawPath(body, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
        // handle
        val handle = Path().apply {
            moveTo(0.37f * w, 0.36f * h)
            quadraticTo(0.37f * w, 0.18f * h, 0.5f * w, 0.18f * h)
            quadraticTo(0.63f * w, 0.18f * h, 0.63f * w, 0.36f * h)
        }
        drawPath(handle, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** A back chevron. */
@Composable
fun BackGlyph(color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        val sw = size.minDimension * 0.1f
        val p = Path().apply {
            moveTo(0.6f * w, 0.22f * h)
            lineTo(0.36f * w, 0.5f * h)
            lineTo(0.6f * w, 0.78f * h)
        }
        drawPath(p, color, style = Stroke(width = sw, cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/** The FauxCart brand mark (target + arrow burst). Replace the PNG at the same path to rebrand. */
@Composable
fun FauxLogo(modifier: Modifier = Modifier, sizeDp: Int = 32) {
    Image(
        painter = painterResource(Res.drawable.fauxcart_logo),
        contentDescription = "FauxCart",
        modifier = modifier.size(sizeDp.dp),
        contentScale = ContentScale.Fit,
    )
}
