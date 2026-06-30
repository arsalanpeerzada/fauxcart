package com.arsalan.fake_ecommerce.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.arsalan.fake_ecommerce.domain.model.Product

/**
 * Stylised product imagery. To keep the project dependency-light and runnable on all targets out of
 * the box, this renders a deterministic gradient tile with the product's initials rather than a
 * network image. The product carries a real `imageUrl` (picsum), so dropping in a multiplatform
 * image loader (e.g. Coil 3 `coil-compose`) and replacing the Box body is a single-spot change.
 */
@Composable
fun ProductImage(
    product: Product,
    modifier: Modifier = Modifier,
    cornerRadiusDp: Int = 20,
) {
    val colors = remember(product.id) { gradientFor(product.id) }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadiusDp.dp))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials(product.name),
            color = Color.White.copy(alpha = 0.92f),
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
        )
    }
}

private fun initials(name: String): String =
    name.split(' ')
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")

private fun gradientFor(id: String): List<Color> {
    // Category family from the id prefix, with a hash-driven hue shift for variety.
    val family = id.substringBefore('_')
    val base = when (family) {
        "frag" -> listOf(Color(0xFF7B5BE0), Color(0xFFB05CD6))
        "tech" -> listOf(Color(0xFF2E6BE6), Color(0xFF36C7C0))
        "life" -> listOf(Color(0xFFE0883B), Color(0xFFE05C8C))
        else -> listOf(Color(0xFF5C6BC0), Color(0xFF26A69A))
    }
    val h = id.fold(0) { acc, c -> (acc * 31 + c.code) and 0xFFFFFF }
    val shift = (h % 40 - 20)
    return base.map { it.shiftLightness(shift) }
}

private fun Color.shiftLightness(delta: Int): Color {
    val f = 1f + delta / 255f
    return Color(
        red = (red * f).coerceIn(0f, 1f),
        green = (green * f).coerceIn(0f, 1f),
        blue = (blue * f).coerceIn(0f, 1f),
        alpha = alpha,
    )
}
