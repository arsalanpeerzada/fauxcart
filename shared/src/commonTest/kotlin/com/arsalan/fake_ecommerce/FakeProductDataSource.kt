package com.arsalan.fake_ecommerce

import com.arsalan.fake_ecommerce.data.source.ProductDataSource
import com.arsalan.fake_ecommerce.domain.model.Product

/** Test double for [ProductDataSource]. Records how many times the source was read. */
class FakeProductDataSource(
    private val base: List<Product> = sampleBase,
) : ProductDataSource {
    var loadCount: Int = 0
        private set

    override suspend fun loadBaseProducts(): List<Product> {
        loadCount++
        return base
    }

    companion object {
        val sampleBase = listOf(
            Product("frag_001", "A", 100.0, "", ""),
            Product("tech_001", "B", 200.0, "", ""),
            Product("life_001", "C", 50.0, "", ""),
        )
    }
}
