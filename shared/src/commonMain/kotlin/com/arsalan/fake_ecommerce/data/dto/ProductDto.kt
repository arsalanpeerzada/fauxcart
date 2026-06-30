package com.arsalan.fake_ecommerce.data.dto

import com.arsalan.fake_ecommerce.domain.model.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Wire model that mirrors the JSON file exactly. Kept separate from the domain [Product] so the
 * serialization format can change without touching the rest of the app (Single Responsibility,
 * and it keeps kotlinx.serialization annotations out of the domain).
 */
@Serializable
data class ProductDto(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    @SerialName("imageUrl") val imageUrl: String,
) {
    fun toDomain(): Product = Product(
        id = id,
        name = name,
        price = price,
        description = description,
        imageUrl = imageUrl,
    )
}
