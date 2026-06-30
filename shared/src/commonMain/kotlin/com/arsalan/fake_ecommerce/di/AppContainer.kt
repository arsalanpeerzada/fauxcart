package com.arsalan.fake_ecommerce.di

import com.arsalan.fake_ecommerce.data.local.provideSavingsStore
import com.arsalan.fake_ecommerce.data.repository.ProductRepositoryImpl
import com.arsalan.fake_ecommerce.data.repository.SavingsRepositoryImpl
import com.arsalan.fake_ecommerce.data.source.JsonProductDataSource
import com.arsalan.fake_ecommerce.data.source.ProductDataSource
import com.arsalan.fake_ecommerce.data.local.SavingsStore
import com.arsalan.fake_ecommerce.domain.repository.ProductRepository
import com.arsalan.fake_ecommerce.domain.repository.SavingsRepository

/**
 * Lightweight manual dependency container - the composition root that wires concrete
 * implementations to the interfaces the rest of the app depends on (Dependency Inversion).
 * Defaults are provided but every collaborator can be overridden (handy for tests/previews).
 */
class AppContainer(
    productDataSource: ProductDataSource = JsonProductDataSource(),
    savingsStore: SavingsStore = provideSavingsStore(),
) {
    val productRepository: ProductRepository = ProductRepositoryImpl(productDataSource)
    val savingsRepository: SavingsRepository = SavingsRepositoryImpl(savingsStore)
}
