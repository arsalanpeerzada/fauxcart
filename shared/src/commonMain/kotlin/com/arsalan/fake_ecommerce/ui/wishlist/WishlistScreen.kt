package com.arsalan.fake_ecommerce.ui.wishlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.HeartGlyph
import com.arsalan.fake_ecommerce.ui.components.ProductCard
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens

private val BottomBarClearance = 112.dp

/** The saved items. Tapping a card opens its detail, where it can be un-saved. */
@Composable
fun WishlistScreen(
    state: ShoppingUiState,
    onOpenDetail: (String) -> Unit,
) {
    val items = state.wishlistedProducts()
    if (items.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                HeartGlyph(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(56.dp),
                )
                Text("Nothing saved yet", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Tap the heart on anything you fancy. Saving it here is free - and so is everything else.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 168.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = BottomBarClearance),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(items, key = { it.id }) { product ->
            ProductCard(
                product = product,
                onClick = { onOpenDetail(product.id) },
                modifier = Modifier.animateItem(
                    fadeInSpec = MotionTokens.smooth(MotionTokens.DurationMedium),
                    placementSpec = MotionTokens.snappy(),
                ),
            )
        }
    }
}
