package com.arsalan.fake_ecommerce.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arsalan.fake_ecommerce.di.AppContainer
import com.arsalan.fake_ecommerce.domain.model.Screen
import com.arsalan.fake_ecommerce.domain.model.isTabRoot
import com.arsalan.fake_ecommerce.domain.model.tab
import com.arsalan.fake_ecommerce.presentation.ShoppingViewModel
import com.arsalan.fake_ecommerce.ui.cart.CartScreen
import com.arsalan.fake_ecommerce.ui.checkout.CheckoutScreen
import com.arsalan.fake_ecommerce.ui.components.BackGlyph
import com.arsalan.fake_ecommerce.ui.components.BagGlyph
import com.arsalan.fake_ecommerce.ui.components.FauxBottomBar
import com.arsalan.fake_ecommerce.ui.components.FauxLogo
import com.arsalan.fake_ecommerce.ui.detail.DetailScreen
import com.arsalan.fake_ecommerce.ui.home.HomeScreen
import com.arsalan.fake_ecommerce.ui.notifications.NotificationsScreen
import com.arsalan.fake_ecommerce.ui.profile.ProfileScreen
import com.arsalan.fake_ecommerce.ui.reward.RewardScreen
import com.arsalan.fake_ecommerce.ui.theme.FauxCartTheme
import com.arsalan.fake_ecommerce.ui.theme.LocalReduceMotion
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens
import com.arsalan.fake_ecommerce.ui.wishlist.WishlistScreen
import kotlinx.coroutines.delay

private const val SPLASH_DURATION_MS = 1700L

/**
 * Application root. Shows a brief branded splash, then crossfades into the main app. The app owns
 * the theme, obtains the [ShoppingViewModel] (which survives rotation), and renders the current
 * [Screen] with directional, animated transitions. A floating bottom navigation bar sits over the
 * four tab roots and slides away during the pushed flows (Detail, Cart, Checkout, Reward).
 */
@Composable
fun FauxCartApp(container: AppContainer = remember { AppContainer() }) {
    FauxCartTheme {
        var showSplash by remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            delay(SPLASH_DURATION_MS)
            showSplash = false
        }
        Crossfade(targetState = showSplash, label = "splash") { splash ->
            if (splash) SplashScreen() else MainContent(container)
        }
    }
}

@Composable
private fun MainContent(container: AppContainer) {
    val viewModel: ShoppingViewModel = viewModel {
        ShoppingViewModel(container.productRepository, container.savingsRepository)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                FauxTopBar(
                    screen = state.screen,
                    cartCount = state.cartCount,
                    onBack = viewModel::back,
                    onOpenCart = viewModel::openCart,
                )
            },
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding).fillMaxSize()) {
                AnimatedContent(
                    targetState = state.screen,
                    transitionSpec = {
                        if (initialState.isTabRoot() && targetState.isTabRoot()) {
                            fadeIn(MotionTokens.smooth()) togetherWith fadeOut(MotionTokens.smooth())
                        } else {
                            val forward = screenDepth(targetState) >= screenDepth(initialState)
                            val direction = if (forward) SlideDirection.Left else SlideDirection.Right
                            (slideIntoContainer(direction) + fadeIn(MotionTokens.smooth())) togetherWith
                                (slideOutOfContainer(direction) + fadeOut(MotionTokens.smooth()))
                        }
                    },
                    contentKey = { it.key() },
                    label = "screen",
                ) { screen ->
                    when (screen) {
                        Screen.Home -> HomeScreen(state, onOpenDetail = viewModel::openDetail)
                        Screen.Wishlist -> WishlistScreen(state, onOpenDetail = viewModel::openDetail)
                        Screen.Notifications -> NotificationsScreen(state)
                        Screen.Profile -> ProfileScreen(
                            state = state,
                            onEmailChange = viewModel::updateEmail,
                            onUsernameChange = viewModel::updateUsername,
                        )
                        is Screen.Detail -> DetailScreen(
                            state = state,
                            productId = screen.productId,
                            onAddToCart = viewModel::addToCart,
                            onToggleWishlist = viewModel::toggleWishlist,
                        )
                        Screen.Cart -> CartScreen(
                            state = state,
                            onRemove = viewModel::removeFromCart,
                            onCheckout = viewModel::openCheckout,
                            onKeepBrowsing = viewModel::back,
                        )
                        Screen.Checkout -> CheckoutScreen(state = state, onPlaceOrder = viewModel::placeOrder)
                        Screen.Reward -> RewardScreen(state = state, onNewSession = viewModel::startNewSession)
                    }
                }

                AnimatedVisibility(
                    visible = state.screen.isTabRoot(),
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    FauxBottomBar(
                        selected = state.screen.tab(),
                        onSelect = viewModel::selectTab,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FauxTopBar(
    screen: Screen,
    cartCount: Int,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (screen.isTabRoot()) {
                    FauxLogo(sizeDp = 30, modifier = Modifier.padding(end = 10.dp))
                }
                Text(screenTitle(screen), fontWeight = FontWeight.Bold)
            }
        },
        navigationIcon = {
            if (!screen.isTabRoot()) {
                TopBarButton(onClick = onBack) {
                    BackGlyph(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
                }
            }
        },
        actions = {
            if (screen.isTabRoot() || screen is Screen.Detail) {
                CartAction(count = cartCount, onClick = onOpenCart)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        ),
    )
}

@Composable
private fun TopBarButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
        content = { content() },
    )
}

/** Cart action (bag glyph) with a count badge that pops with a spring when the count changes. */
@Composable
private fun CartAction(count: Int, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        TopBarButton(onClick = onClick) {
            BagGlyph(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(24.dp))
        }
        if (count > 0) {
            val reduce = LocalReduceMotion.current
            val scale = remember { Animatable(1f) }
            LaunchedEffect(count) {
                if (!reduce) {
                    scale.snapTo(1.4f)
                    scale.animateTo(1f, MotionTokens.bouncy())
                }
            }
            Surface(
                color = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
                modifier = Modifier
                    .padding(top = 6.dp, end = 4.dp)
                    .size(18.dp)
                    .graphicsLayer { scaleX = scale.value; scaleY = scale.value },
            ) {
                Box(Modifier.size(18.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = count.toString(),
                        color = MaterialTheme.colorScheme.onSecondary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

private fun screenTitle(screen: Screen): String = when (screen) {
    Screen.Home -> "FauxCart"
    Screen.Wishlist -> "Wishlist"
    Screen.Notifications -> "Notifications"
    Screen.Profile -> "Profile"
    is Screen.Detail -> "Details"
    Screen.Cart -> "Your cart"
    Screen.Checkout -> "Checkout"
    Screen.Reward -> "Nice one"
}

private fun screenDepth(screen: Screen): Int = when (screen) {
    Screen.Home, Screen.Wishlist, Screen.Notifications, Screen.Profile -> 0
    is Screen.Detail -> 1
    Screen.Cart -> 1
    Screen.Checkout -> 2
    Screen.Reward -> 3
}

private fun Screen.key(): String = when (this) {
    Screen.Home -> "home"
    Screen.Wishlist -> "wishlist"
    Screen.Notifications -> "notifications"
    Screen.Profile -> "profile"
    is Screen.Detail -> "detail:$productId"
    Screen.Cart -> "cart"
    Screen.Checkout -> "checkout"
    Screen.Reward -> "reward"
}
