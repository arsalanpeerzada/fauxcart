package com.arsalan.fake_ecommerce.domain.model

/**
 * The four bottom-navigation destinations.
 */
enum class Tab { Home, Wishlist, Notifications, Profile }

/**
 * Top-level destinations. Held inside [ShoppingUiState] so navigation survives configuration
 * changes (the ViewModel outlives rotation). [Detail] carries only the product id, never the
 * whole object, keeping the navigation state small and serialisable.
 *
 * Home, Wishlist, Notifications and Profile are the bottom-bar tab roots; Detail, Cart, Checkout
 * and Reward are flows pushed on top of a tab (the bottom bar hides while they are showing).
 */
sealed interface Screen {
    data object Home : Screen
    data object Wishlist : Screen
    data object Notifications : Screen
    data object Profile : Screen
    data class Detail(val productId: String) : Screen
    data object Cart : Screen
    data object Checkout : Screen
    data object Reward : Screen
}

/** The tab this screen belongs to (used to keep the bottom-bar selection correct). */
fun Screen.tab(): Tab = when (this) {
    Screen.Home, is Screen.Detail, Screen.Cart, Screen.Checkout, Screen.Reward -> Tab.Home
    Screen.Wishlist -> Tab.Wishlist
    Screen.Notifications -> Tab.Notifications
    Screen.Profile -> Tab.Profile
}

/** True for the four tab-root screens that show the bottom bar. */
fun Screen.isTabRoot(): Boolean = this is Screen.Home || this is Screen.Wishlist ||
    this is Screen.Notifications || this is Screen.Profile

/** The root screen for a given tab. */
fun Tab.rootScreen(): Screen = when (this) {
    Tab.Home -> Screen.Home
    Tab.Wishlist -> Screen.Wishlist
    Tab.Notifications -> Screen.Notifications
    Tab.Profile -> Screen.Profile
}

/**
 * Sub-states of the checkout flow. Kept separate from [Screen] so the checkout screen can animate
 * between its internal stages with AnimatedContent without changing the top-level destination.
 */
enum class CheckoutStage { Form, Processing, Tracking }
