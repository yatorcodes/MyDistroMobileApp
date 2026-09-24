package com.emmanuelyator.mydistro.feature.customer.domain

import com.emmanuelyator.mydistro.core.model.CategoryGroup
import com.emmanuelyator.mydistro.core.model.Product
import kotlinx.coroutines.flow.Flow

interface CatalogRepository {
    fun observeGroups(): Flow<List<CategoryGroup>>
    fun observeProducts(
        categoryId: String?,
        query: String
    ): Flow<List<Product>>
}

/**
 * In-memory cart for the prototype. Real checkout will sync with the backend;
 * this only exists so "Add" has an immediate, visible effect.
 */
interface CartRepository {
    val itemCount: Flow<Int>
    suspend fun addProduct(productId: String, quantity: Int = 1)
    suspend fun clear()
}
