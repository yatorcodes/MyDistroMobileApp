package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.model.CategoryGroup
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.feature.customer.domain.CartRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CatalogRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Singleton
class MockCatalogRepository @Inject constructor() : CatalogRepository {

    override fun observeGroups(): Flow<List<CategoryGroup>> =
        flowOf(MockCatalogData.groups)

    override fun observeProducts(
        categoryId: String?,
        query: String
    ): Flow<List<Product>> {
        val normalised = query.trim()
        return flowOf(MockCatalogData.products).map { all ->
            all
                .filter { product ->
                    categoryId == null || product.categoryId == categoryId
                }
                .filter { product ->
                    if (normalised.isEmpty()) {
                        true
                    } else {
                        product.name.contains(normalised, ignoreCase = true) ||
                            product.brand.contains(normalised, ignoreCase = true)
                    }
                }
        }
    }
}

@Singleton
class InMemoryCartRepository @Inject constructor() : CartRepository {

    private val _quantities = MutableStateFlow<Map<String, Int>>(emptyMap())

    override val quantities: Flow<Map<String, Int>> = this._quantities

    override val itemCount: Flow<Int> = this._quantities.map { map -> map.values.sum() }

    override suspend fun addProduct(productId: String, quantity: Int) {
        val next = _quantities.value.toMutableMap()
        next[productId] = (next[productId] ?: 0) + quantity
        _quantities.value = next
    }

    override suspend fun removeProduct(productId: String) {
        val next = _quantities.value.toMutableMap()
        next.remove(productId)
        _quantities.value = next
    }

    override suspend fun updateQuantity(productId: String, quantity: Int) {
        val next = _quantities.value.toMutableMap()
        if (quantity <= 0) {
            next.remove(productId)
        } else {
            next[productId] = quantity
        }
        _quantities.value = next
    }

    override suspend fun clear() {
        _quantities.value = emptyMap()
    }
}
