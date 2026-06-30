package com.arsalan.fake_ecommerce.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsalan.fake_ecommerce.domain.model.CheckoutStage
import com.arsalan.fake_ecommerce.domain.model.OrderRecord
import com.arsalan.fake_ecommerce.domain.model.Product
import com.arsalan.fake_ecommerce.domain.model.Screen
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.domain.model.Tab
import com.arsalan.fake_ecommerce.domain.model.rootScreen
import com.arsalan.fake_ecommerce.domain.repository.ProductRepository
import com.arsalan.fake_ecommerce.domain.repository.SavingsRepository
import com.arsalan.fake_ecommerce.domain.usecase.SavingsCalculator
import com.arsalan.fake_ecommerce.util.nowMillis
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The single orchestrator of application state. It holds the only mutable state (private
 * MutableStateFlow) and exposes a read-only StateFlow; the View can never mutate state directly
 * (encapsulation). Every change goes through `_state.update { ... }`, the one mutation path that
 * guarantees [ShoppingUiState] is always published as a complete, consistent snapshot.
 *
 * It survives screen rotation because AndroidX [ViewModel] outlives configuration changes, so the
 * cart, ordering progress and delivery progress are all retained automatically.
 *
 * All "network" behaviour (payment, delivery) is simulated with coroutine delays in [viewModelScope].
 */
class ShoppingViewModel(
    private val productRepository: ProductRepository,
    private val savingsRepository: SavingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ShoppingUiState())
    val state: StateFlow<ShoppingUiState> = _state.asStateFlow()

    private var orderJob: Job? = null

    init {
        loadCatalog()
        loadSavings()
    }

    // ---- Catalogue ---------------------------------------------------------------------------

    fun loadCatalog() {
        viewModelScope.launch {
            _state.update { it.copy(isCatalogLoading = true, catalogError = null) }
            productRepository.getCatalog()
                .onSuccess { items ->
                    _state.update { it.copy(isCatalogLoading = false, catalogItems = items) }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(isCatalogLoading = false, catalogError = error.message ?: "Failed to load catalogue")
                    }
                }
        }
    }

    private fun loadSavings() {
        viewModelScope.launch {
            val saved = savingsRepository.load()
            _state.update { it.copy(totalSaved = saved.totalSaved, streak = saved.streak) }
        }
    }

    // ---- Navigation --------------------------------------------------------------------------

    /** Switches the active bottom-bar tab to its root screen. */
    fun selectTab(tab: Tab) = navigateTo(tab.rootScreen())

    fun openDetail(productId: String) = navigateTo(Screen.Detail(productId))
    fun openCart() = navigateTo(Screen.Cart)
    fun openCheckout() {
        if (_state.value.isCartEmpty) return
        _state.update { it.copy(screen = Screen.Checkout, checkoutStage = CheckoutStage.Form) }
    }

    fun back() {
        _state.update {
            val target = when (it.screen) {
                is Screen.Detail -> Screen.Home
                Screen.Cart -> Screen.Home
                Screen.Checkout -> Screen.Cart
                Screen.Reward -> Screen.Home
                Screen.Home, Screen.Wishlist, Screen.Notifications, Screen.Profile -> Screen.Home
            }
            it.copy(screen = target)
        }
    }

    private fun navigateTo(screen: Screen) = _state.update { it.copy(screen = screen) }

    // ---- Wishlist & profile ------------------------------------------------------------------

    fun toggleWishlist(productId: String) = _state.update {
        val next = it.wishlistIds.toMutableSet()
        if (!next.add(productId)) next.remove(productId)
        it.copy(wishlistIds = next)
    }

    fun updateEmail(value: String) = _state.update { it.copy(email = value) }
    fun updateUsername(value: String) = _state.update { it.copy(username = value) }

    // ---- Cart --------------------------------------------------------------------------------

    fun addToCart(product: Product) =
        _state.update { it.copy(cartItems = it.cartItems + product) }

    /** Removes the cart line at [index] (cart allows duplicates, so we remove by position). */
    fun removeFromCart(index: Int) = _state.update {
        if (index !in it.cartItems.indices) it
        else it.copy(cartItems = it.cartItems.toMutableList().apply { removeAt(index) })
    }

    // ---- Simulated checkout, payment and delivery --------------------------------------------

    /** Starts the simulated order. Guarded so it cannot run with an empty cart or run twice. */
    fun placeOrder() {
        val current = _state.value
        if (current.isCartEmpty || current.isOrdering) return

        val avoided = SavingsCalculator.amountAvoided(current.cartItems)
        val itemCount = current.cartItems.size

        orderJob?.cancel()
        orderJob = viewModelScope.launch {
            // Processing
            _state.update { it.copy(isOrdering = true, checkoutStage = CheckoutStage.Processing, deliveryProgress = 0f) }
            delay(PAYMENT_DELAY_MS)

            // Delivery tracking - progress drives the tracker animation and survives rotation
            _state.update { it.copy(checkoutStage = CheckoutStage.Tracking) }
            repeat(DELIVERY_STEPS) { step ->
                delay(DELIVERY_STEP_MS)
                _state.update { it.copy(deliveryProgress = (step + 1f) / DELIVERY_STEPS) }
            }

            // Persist the win, then reframe around savings
            val saved = savingsRepository.recordAvoidedPurchase(avoided)
            _state.update {
                val record = OrderRecord(
                    id = it.orders.size + 1,
                    itemCount = itemCount,
                    amountSaved = avoided,
                    timestampMillis = nowMillis(),
                )
                it.copy(
                    isOrdering = false,
                    cartItems = emptyList(),
                    orders = it.orders + record,
                    lastOrderSaved = avoided,
                    totalSaved = saved.totalSaved,
                    streak = saved.streak,
                    screen = Screen.Reward,
                    deliveryProgress = 1f,
                )
            }
        }
    }

    /** Returns to browsing for another session, keeping cumulative savings and streak. */
    fun startNewSession() {
        orderJob?.cancel()
        _state.update {
            it.copy(
                screen = Screen.Home,
                checkoutStage = CheckoutStage.Form,
                isOrdering = false,
                deliveryProgress = 0f,
                lastOrderSaved = 0.0,
            )
        }
    }

    companion object {
        const val PAYMENT_DELAY_MS = 2000L
        const val DELIVERY_STEPS = 50
        const val DELIVERY_STEP_MS = 50L // 50 x 50ms = ~2.5s tracking animation
    }
}
