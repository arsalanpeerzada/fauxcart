package com.arsalan.fake_ecommerce.data.source

import com.arsalan.fake_ecommerce.domain.model.Product

/**
 * Reads the raw baseline products. Its single responsibility is loading + parsing; it knows
 * nothing about catalogue inflation, the cart, or the UI. Being an interface lets tests and
 * previews substitute a fake source (Liskov, Dependency Inversion).
 */
interface ProductDataSource {
    /** Returns the small set of detailed baseline products (~20 items). */
    suspend fun loadBaseProducts(): List<Product>
}
