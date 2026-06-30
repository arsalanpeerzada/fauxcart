package com.arsalan.fake_ecommerce.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.Product
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens

/**
 * Catalogue tile. The image area accepts its own [imageModifier] so callers can attach a
 * shared-element modifier for the detail transition without this component knowing about navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    imageModifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val reduce = LocalReduceMotion.current
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reduce) 0.96f else 1f,
        animationSpec = MotionTokens.snappy(),
        label = "cardPressScale",
    )

    Card(
        onClick = onClick,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        interactionSource = interaction,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column {
            ProductImage(
                product = product,
                modifier = imageModifier.fillMaxWidth().aspectRatio(1f),
            )
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp),
            )
            PriceText(
                amount = product.price,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 2.dp, bottom = 12.dp),
            )
        }
    }
}
