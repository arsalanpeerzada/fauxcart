package com.arsalan.fake_ecommerce.domain.repository

import com.arsalan.fake_ecommerce.domain.model.Product

/**
 * Abstraction the presentation layer depends on (Dependency Inversion). The ViewModel knows only
 * this interface, never that the data comes from a bundled JSON file - that detail lives in the
 * data layer. This is what keeps JSON reading out of the UI entirely.
 *
 * Kept focused per the Interface Segregation Principle: it only serves the product catalogue.
 */
interface ProductRepository {
    /** Loads (and, in the implementation, inflates) the full catalogue. */
    suspend fun getCatalog(): Result<List<Product>>
}
