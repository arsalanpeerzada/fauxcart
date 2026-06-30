package com.arsalan.fake_ecommerce.data.repository

import com.arsalan.fake_ecommerce.data.source.ProductDataSource
import com.arsalan.fake_ecommerce.domain.model.Product
import com.arsalan.fake_ecommerce.domain.repository.ProductRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Owns catalogue inflation: it takes the ~20 detailed baseline products and expands them into a
 * large, immersive catalogue by appending a unique index suffix to each id, so every item is
 * distinct. This logic lives in exactly one place (DRY) and is unit-testable in isolation.
 *
 * The result is computed once and cached; the mutex makes the first concurrent load safe
 * (the "Isolation" of the ACID-in-spirit model).
 */
class ProductRepositoryImpl(
    private val dataSource: ProductDataSource,
    private val copies: Int = DEFAULT_COPIES,
) : ProductRepository {

    private val cacheLock = Mutex()
    private var cached: List<Product>? = null

    override suspend fun getCatalog(): Result<List<Product>> = runCatching {
        cacheLock.withLock {
            cached ?: inflate(dataSource.loadBaseProducts()).also { cached = it }
        }
    }

    private fun inflate(base: List<Product>): List<Product> =
        List(copies) { index ->
            base.map { product -> product.copy(id = "${product.id}_$index") }
        }.flatten()

    companion object {
        /** 20 baseline items x 14 = 280 unique items, within the 200-300 target range. */
        const val DEFAULT_COPIES: Int = 14
    }
}
