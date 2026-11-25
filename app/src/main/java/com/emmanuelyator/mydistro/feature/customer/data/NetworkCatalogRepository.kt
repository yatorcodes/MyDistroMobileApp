package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.BuildConfig
import com.emmanuelyator.mydistro.core.model.CategoryGroup
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.core.model.ProductCategory
import com.emmanuelyator.mydistro.feature.customer.domain.CatalogRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/**
 * Loads distributor catalog offers from `GET /api/v1/products`.
 * Product ids are stock-item UUIDs and are valid for [OrderItemRequest.stockItemId].
 */
@Singleton
class NetworkCatalogRepository @Inject constructor(
    private val api: CustomerApiService
) : CatalogRepository {

    private val products = MutableStateFlow<List<Product>>(emptyList())
    private val groups = MutableStateFlow<List<CategoryGroup>>(emptyList())

    override fun observeGroups(): Flow<List<CategoryGroup>> = groups

    override fun observeProducts(categoryId: String?, query: String): Flow<List<Product>> =
        products.map { all ->
            all.filter { product ->
                val categoryOk = categoryId.isNullOrBlank() || product.categoryId == categoryId
                val queryOk = query.isBlank() ||
                    product.name.contains(query, ignoreCase = true) ||
                    product.brand.contains(query, ignoreCase = true)
                categoryOk && queryOk
            }
        }

    override suspend fun refresh() {
        val categoriesResponse = api.getCategories()
        val productsResponse = api.getProducts(size = 100)
        if (!productsResponse.isSuccessful) {
            error("Products failed: ${productsResponse.code()}")
        }
        val dtos = productsResponse.body()?.content.orEmpty()
        val mapped = dtos.map { it.toProduct() }
        products.value = mapped

        val categoryNames = if (categoriesResponse.isSuccessful) {
            categoriesResponse.body().orEmpty().filter { it.isNotBlank() }
        } else {
            mapped.map { it.categoryId }.distinct()
        }
        groups.value = buildGroups(categoryNames.ifEmpty { mapped.map { it.categoryId }.distinct() })
    }

    private fun buildGroups(categoryNames: List<String>): List<CategoryGroup> {
        val children = categoryNames.distinct().sorted().map { name ->
            ProductCategory(id = name, name = name, groupId = GROUP_ALL)
        }
        return listOf(
            CategoryGroup(
                id = GROUP_ALL,
                name = "Catalogue",
                children = children.ifEmpty {
                    listOf(ProductCategory(id = "All", name = "All", groupId = GROUP_ALL))
                }
            )
        )
    }

    private fun ProductDto.toProduct(): Product {
        val price = unitPrice?.toInt() ?: 0
        val category = category?.takeIf { it.isNotBlank() } ?: "Other"
        val image = imageUrl?.let { url ->
            when {
                url.startsWith("http") -> url
                url.startsWith("/") -> BuildConfig.BASE_URL.trimEnd('/') + url
                else -> BuildConfig.BASE_URL.trimEnd('/') + "/" + url
            }
        }
        return Product(
            id = id,
            name = name,
            brand = brand.orEmpty().ifBlank { "—" },
            categoryId = category,
            unitLabel = unit.orEmpty().ifBlank { "unit" },
            priceKes = price,
            imageGallery = listOfNotNull(image),
            inStock = inStock ?: true,
            organizationId = organizationId.orEmpty()
        )
    }

    companion object {
        const val GROUP_ALL = "grp-all"
    }
}
