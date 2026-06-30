package com.arsalan.fake_ecommerce.ui.checkout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.CheckoutStage
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.LottieAssets
import com.arsalan.fake_ecommerce.ui.components.LottieBox
import com.arsalan.fake_ecommerce.ui.components.PriceText
import com.arsalan.fake_ecommerce.ui.components.PrimaryButton
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens

/**
 * Checkout. Animates between its internal stages (form -> processing -> tracking) with
 * AnimatedContent. The processing and delivery visuals are Lottie animations; the tracker's
 * progress is bound to [ShoppingUiState.deliveryProgress], which is driven by the ViewModel and
 * therefore survives rotation.
 *
 * Note: the checkout fields are simulated. They live only in local UI state and are never stored
 * or transmitted - no real personal or payment data is collected.
 */
@Composable
fun CheckoutScreen(
    state: ShoppingUiState,
    onPlaceOrder: () -> Unit,
) {
    AnimatedContent(
        targetState = state.checkoutStage,
        transitionSpec = {
            (fadeIn(MotionTokens.smooth()) + scaleIn(initialScale = 0.96f)) togetherWith fadeOut(MotionTokens.smooth())
        },
        label = "checkoutStage",
    ) { stage ->
        when (stage) {
            CheckoutStage.Form -> CheckoutForm(total = state.cartTotal, onPlaceOrder = onPlaceOrder)
            CheckoutStage.Processing -> ProcessingStage()
            CheckoutStage.Tracking -> TrackingStage(progress = state.deliveryProgress)
        }
    }
}

@Composable
private fun CheckoutForm(total: Double, onPlaceOrder: () -> Unit) {
    var name by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var card by rememberSaveable { mutableStateOf("") }

    val valid = name.isNotBlank() && address.isNotBlank() && card.filter { it.isDigit() }.length >= 12

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Almost yours", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Fill it in like you mean it. Nothing is charged, stored or sent - that's the whole idea.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Delivery address") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = card,
            onValueChange = { input -> card = input.filter { it.isDigit() }.take(16) },
            label = { Text("Card number (simulated)") },
            singleLine = true,
            isError = card.isNotEmpty() && card.length < 12,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            PriceText(total, style = MaterialTheme.typography.titleLarge)
        }
        PrimaryButton(
            text = "Place order",
            onClick = onPlaceOrder,
            enabled = valid,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ProcessingStage() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            LottieBox(
                assetPath = LottieAssets.Processing,
                modifier = Modifier.size(160.dp),
                fallback = { CircularProgressIndicator() },
            )
            Text("Processing your (pretend) payment...", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun TrackingStage(progress: Float) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LottieBox(
                assetPath = LottieAssets.Delivery,
                modifier = Modifier.size(200.dp),
                fallback = { CircularProgressIndicator() },
            )
            Text("Your rider is on the way", style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
