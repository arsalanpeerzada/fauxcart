package com.arsalan.fake_ecommerce.domain.usecase

import com.arsalan.fake_ecommerce.domain.model.Product

/**
 * Single, testable home for the savings rule (Single Responsibility). The "saved" figure is the
 * total the user avoided spending by not completing the purchase - i.e. the cart subtotal.
 * Centralised here so screens and the ViewModel never re-implement it (DRY).
 */
object SavingsCalculator {
    fun amountAvoided(cartItems: List<Product>): Double = cartItems.sumOf { it.price }
}
