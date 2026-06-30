package com.arsalan.fake_ecommerce

import com.arsalan.fake_ecommerce.domain.model.Product
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.domain.usecase.SavingsCalculator
import com.arsalan.fake_ecommerce.ui.components.formatUsd
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SavingsAndStateTest {

    private fun product(price: Double) = Product("id", "n", price, "", "")

    @Test
    fun amount_avoided_is_cart_subtotal() {
        val cart = listOf(product(145.0), product(95.50), product(20.0))
        assertEquals(260.50, SavingsCalculator.amountAvoided(cart), 0.0001)
    }

    @Test
    fun empty_cart_avoids_nothing() {
        assertEquals(0.0, SavingsCalculator.amountAvoided(emptyList()), 0.0001)
    }

    @Test
    fun uiState_cart_derivations_are_consistent() {
        val empty = ShoppingUiState()
        assertTrue(empty.isCartEmpty)
        assertEquals(0, empty.cartCount)
        assertEquals(0.0, empty.cartTotal, 0.0001)

        val withItems = empty.copy(cartItems = listOf(product(10.0), product(2.5)))
        assertFalse(withItems.isCartEmpty)
        assertEquals(2, withItems.cartCount)
        assertEquals(12.5, withItems.cartTotal, 0.0001)
    }

    @Test
    fun formatUsd_groups_thousands_and_pads_cents() {
        assertEquals("$1,250.00", formatUsd(1250.0))
        assertEquals("$0.05", formatUsd(0.05))
        assertEquals("$99.90", formatUsd(99.9))
        assertEquals("$1,000,000.00", formatUsd(1_000_000.0))
    }
}
