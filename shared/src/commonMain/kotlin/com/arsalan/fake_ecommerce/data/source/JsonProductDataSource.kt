package com.arsalan.fake_ecommerce.data.source

import com.arsalan.fake_ecommerce.data.dto.ProductDto
import com.arsalan.fake_ecommerce.domain.model.Product
import fakeecommerce.shared.generated.resources.Res
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * The only place in the app that touches the JSON resource. It reads
 * `composeResources/files/products.json`, deserialises it via kotlinx.serialization and maps the
 * DTOs to domain models. The UI never sees any of this - it goes through the repository.
 */
class JsonProductDataSource(
    private val resourcePath: String = "files/products.json",
    private val parser: Json = DefaultJson,
) : ProductDataSource {

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun loadBaseProducts(): List<Product> {
        val raw = Res.readBytes(resourcePath).decodeToString()
        return parser.decodeFromString<List<ProductDto>>(raw).map(ProductDto::toDomain)
    }

    companion object {
        val DefaultJson: Json = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
}
