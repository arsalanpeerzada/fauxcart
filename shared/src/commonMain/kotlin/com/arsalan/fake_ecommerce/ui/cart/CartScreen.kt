package com.arsalan.fake_ecommerce.ui.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.Product
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.PriceText
import com.arsalan.fake_ecommerce.ui.components.PrimaryButton
import com.arsalan.fake_ecommerce.ui.components.ProductImage

@Composable
fun CartScreen(
    state: ShoppingUiState,
    onRemove: (Int) -> Unit,
    onCheckout: () -> Unit,
    onKeepBrowsing: () -> Unit,
) {
    if (state.isCartEmpty) {
        EmptyCart(onKeepBrowsing)
        return
    }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(state.cartItems) { index, product ->
                CartRow(
                    product = product,
                    onRemove = { onRemove(index) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
        CheckoutBar(total = state.cartTotal, onCheckout = onCheckout)
    }
}

@Composable
private fun CartRow(product: Product, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ProductImage(product, cornerRadiusDp = 14, modifier = Modifier.size(56.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                PriceText(product.price, style = MaterialTheme.typography.bodyMedium)
            }
            TextButton(onClick = onRemove) { Text("Remove") }
        }
    }
}

@Composable
private fun CheckoutBar(total: Double, onCheckout: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 3.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("You're about to save", style = MaterialTheme.typography.titleMedium)
                PriceText(total, style = MaterialTheme.typography.titleMedium)
            }
            PrimaryButton(text = "Checkout (keep the money)", onClick = onCheckout, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun EmptyCart(onKeepBrowsing: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Your cart is empty", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                "Add a few things you're craving, then check out for free.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            PrimaryButton(text = "Keep browsing", onClick = onKeepBrowsing)
        }
    }
}
