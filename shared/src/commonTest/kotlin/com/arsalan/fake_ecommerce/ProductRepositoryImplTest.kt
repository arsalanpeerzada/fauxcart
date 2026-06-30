package com.arsalan.fake_ecommerce

import com.arsalan.fake_ecommerce.data.repository.ProductRepositoryImpl
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductRepositoryImplTest {

    @Test
    fun inflates_base_into_unique_catalogue() = runTest {
        val source = FakeProductDataSource() // 3 base items
        val repo = ProductRepositoryImpl(source, copies = 14)

        val catalogue = repo.getCatalog().getOrThrow()

        assertEquals(3 * 14, catalogue.size, "should be base x copies items")
        assertEquals(catalogue.size, catalogue.map { it.id }.toSet().size, "all ids must be unique")
        assertTrue(catalogue.all { it.id.contains('_') }, "ids carry an index suffix")
    }

    @Test
    fun default_copies_land_in_target_range() = runTest {
        // 20 baseline items x DEFAULT_COPIES should sit within the 200-300 SRS target.
        val total = 20 * ProductRepositoryImpl.DEFAULT_COPIES
        assertTrue(total in 200..300, "expected 200-300, was $total")
    }

    @Test
    fun catalogue_is_cached_after_first_load() = runTest {
        val source = FakeProductDataSource()
        val repo = ProductRepositoryImpl(source, copies = 2)

        repo.getCatalog().getOrThrow()
        repo.getCatalog().getOrThrow()

        assertEquals(1, source.loadCount, "data source should be read only once")
    }
}
