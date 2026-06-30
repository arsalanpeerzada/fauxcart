package com.arsalan.fake_ecommerce.ui.detail

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import com.arsalan.fake_ecommerce.domain.model.Product
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.HeartGlyph
import com.arsalan.fake_ecommerce.ui.components.PriceText
import com.arsalan.fake_ecommerce.ui.components.PrimaryButton
import com.arsalan.fake_ecommerce.ui.components.ProductImage
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Product detail. The hero image springs up on entry (a hero-expand feel), and adding to the cart
 * fires a "fly to cart" chip that arcs toward the top-right cart icon while the cart badge pops.
 */
@Composable
fun DetailScreen(
    state: ShoppingUiState,
    productId: String,
    onAddToCart: (Product) -> Unit,
    onToggleWishlist: (String) -> Unit,
) {
    val product = state.productById(productId)
    if (product == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Item not found")
        }
        return
    }

    val reduce = LocalReduceMotion.current
    val scope = rememberCoroutineScope()
    val flyProgress = remember { Animatable(0f) }
    var flying by remember { mutableStateOf(false) }

    val heroScale = remember { Animatable(0.9f) }
    LaunchedEffect(productId) {
        heroScale.snapTo(if (reduce) 1f else 0.9f)
        if (!reduce) heroScale.animateTo(1f, MotionTokens.bouncy())
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProductImage(
                product = product,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .graphicsLayer { scaleX = heroScale.value; scaleY = heroScale.value },
            )
            Text(product.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            PriceText(product.price, style = MaterialTheme.typography.titleLarge)

            val wishlisted = state.isWishlisted(productId)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onToggleWishlist(productId) }
                    .padding(vertical = 4.dp, horizontal = 4.dp),
            ) {
                HeartGlyph(
                    color = if (wishlisted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    filled = wishlisted,
                    modifier = Modifier.size(22.dp),
                )
                Text(
                    if (wishlisted) "Saved to wishlist" else "Save to wishlist",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                product.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            PrimaryButton(
                text = "Add to cart",
                onClick = {
                    onAddToCart(product)
                    if (!reduce) scope.launch {
                        flying = true
                        flyProgress.snapTo(0f)
                        flyProgress.animateTo(1f, tween(durationMillis = 620))
                        flying = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            )
            if (state.cartCount > 0) {
                Text(
                    "${state.cartCount} in cart - tap the cart to checkout (it's free, that's the point)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (flying) {
            FlyingChip(product = product, progress = flyProgress.value)
        }
    }
}

/** A small product chip that arcs from the lower-centre toward the top-right (the cart icon). */
@Composable
private fun FlyingChip(product: Product, progress: Float) {
    val density = LocalDensity.current
    val chipPx = with(density) { 64.dp.toPx() }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = constraints.maxWidth.toFloat()
        val h = constraints.maxHeight.toFloat()
        val startX = w * 0.5f
        val startY = h * 0.78f
        val endX = w * 0.93f
        val endY = 0f
        val x = lerp(startX, endX, progress)
        val arc = (sin(progress * PI).toFloat()) * 140f
        val y = lerp(startY, endY, progress) - arc
        val scale = lerp(1f, 0.3f, progress)
        val alpha = if (progress < 0.85f) 1f else lerp(1f, 0f, (progress - 0.85f) / 0.15f)
        ProductImage(
            product = product,
            cornerRadiusDp = 18,
            modifier = Modifier
                .size(64.dp)
                .offset { IntOffset((x - chipPx / 2f).roundToInt(), (y - chipPx / 2f).roundToInt()) }
                .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha },
        )
    }
}
