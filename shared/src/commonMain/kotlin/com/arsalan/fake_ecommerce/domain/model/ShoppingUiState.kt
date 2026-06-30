package com.arsalan.fake_ecommerce.domain.model

/**
 * The single source of truth for the UI. One immutable object describes everything the UI needs
 * to render at a moment in time; the View never mutates it, it only renders it and emits intents.
 *
 * Invariants (the "Consistency" of the ACID-in-spirit model in the SRS) are enforced by always
 * producing a fresh, complete copy through the ViewModel's single mutation path - illegal states
 * such as "ordering with an empty cart" are never published.
 */
data class ShoppingUiState(
    val isCatalogLoading: Boolean = true,
    val catalogError: String? = null,
    val catalogItems: List<Product> = emptyList(),
    val cartItems: List<Product> = emptyList(),
    val wishlistIds: Set<String> = emptySet(),
    val orders: List<OrderRecord> = emptyList(),
    val screen: Screen = Screen.Home,
    val checkoutStage: CheckoutStage = CheckoutStage.Form,
    val isOrdering: Boolean = false,
    val deliveryProgress: Float = 0f, // 0.0 .. 1.0 for the tracking animation
    val lastOrderSaved: Double = 0.0, // amount avoided in the most recent "order" (for the count-up)
    val totalSaved: Double = 0.0,
    val streak: Int = 0,
    val email: String = "shopper@fauxcart.app",
    val username: String = "Window Shopper",
) {
    val cartTotal: Double get() = cartItems.sumOf { it.price }
    val cartCount: Int get() = cartItems.size
    val isCartEmpty: Boolean get() = cartItems.isEmpty()
    val ordersPlaced: Int get() = orders.size

    fun productById(id: String): Product? = catalogItems.firstOrNull { it.id == id }
    fun isWishlisted(id: String): Boolean = id in wishlistIds
    fun wishlistedProducts(): List<Product> = catalogItems.filter { it.id in wishlistIds }
}
