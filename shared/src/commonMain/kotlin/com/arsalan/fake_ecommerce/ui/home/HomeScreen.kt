package com.arsalan.fake_ecommerce.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.arsalan.fake_ecommerce.domain.model.Product
import com.arsalan.fake_ecommerce.domain.model.ShoppingUiState
import com.arsalan.fake_ecommerce.ui.components.LottieAssets
import com.arsalan.fake_ecommerce.ui.components.LottieBox
import com.arsalan.fake_ecommerce.ui.components.ProductCard
import com.arsalan.fake_ecommerce.ui.components.formatUsd
import com.arsalan.fake_ecommerce.ui.theme.MotionTokens

private enum class Category(val label: String, val prefix: String?) {
    All("All", null),
    Fragrance("Fragrance", "frag"),
    Tech("Tech", "tech"),
    Lifestyle("Lifestyle", "life"),
}

/** Bottom padding so the last grid row clears the floating bottom bar. */
private val BottomBarClearance = 112.dp

/**
 * The Home tab: category tabs across the top, then an adaptive, animated catalogue grid below.
 * Adaptive columns keep portrait and landscape correct (rotation requirement).
 */
@Composable
fun HomeScreen(
    state: ShoppingUiState,
    onOpenDetail: (String) -> Unit,
) {
    when {
        state.isCatalogLoading -> CenteredStatus {
            LottieBox(
                assetPath = LottieAssets.Processing,
                modifier = Modifier.size(120.dp),
                fallback = { CircularProgressIndicator() },
            )
            Text("Curating your shelf...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        state.catalogError != null -> CenteredStatus {
            Text("Couldn't load the catalogue", style = MaterialTheme.typography.titleMedium)
            Text(state.catalogError, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        else -> {
            var categoryIndex by rememberSaveable { mutableIntStateOf(0) }
            val categories = Category.entries
            val selected = categories[categoryIndex]
            val items = remember(state.catalogItems, selected) {
                if (selected.prefix == null) state.catalogItems
                else state.catalogItems.filter { it.id.substringBefore('_') == selected.prefix }
            }

            Column(Modifier.fillMaxSize()) {
                TabRow(
                    selectedTabIndex = categoryIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    categories.forEachIndexed { index, category ->
                        Tab(
                            selected = index == categoryIndex,
                            onClick = { categoryIndex = index },
                            text = { Text(category.label) },
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 168.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = BottomBarClearance),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SavingsHeader(totalSaved = state.totalSaved, streak = state.streak)
                    }
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
        }
    }
}

@Composable
private fun SavingsHeader(totalSaved: Double, streak: Int) {
    Column(Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
        Text(
            text = "You've kept ${formatUsd(totalSaved)} in your pocket",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = if (streak > 0) "$streak-session saving streak. Browse freely - checkout costs nothing here." else "Browse freely - checkout costs nothing here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CenteredStatus(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) { content() }
    }
}
