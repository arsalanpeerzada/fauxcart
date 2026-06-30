package com.arsalan.fake_ecommerce.domain.model

/**
 * Core domain entity. Immutable by design: state changes produce copies, never mutate.
 *
 * Note this is deliberately free of serialization annotations - the wire/JSON shape lives in
 * [com.arsalan.fake_ecommerce.data.dto.ProductDto] so the domain stays independent of the
 * data source format (clean architecture, Dependency Inversion).
 */
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String,
)
