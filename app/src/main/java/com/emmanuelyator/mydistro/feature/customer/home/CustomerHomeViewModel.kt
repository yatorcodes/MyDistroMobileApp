package com.emmanuelyator.mydistro.feature.customer.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.model.CategoryGroup
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.core.model.ProductCategory
import com.emmanuelyator.mydistro.feature.customer.domain.CartRepository
import com.emmanuelyator.mydistro.feature.customer.domain.CatalogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BrowseUiState(
    val groups: List<CategoryGroup> = emptyList(),
    val selectedGroupId: String? = null,
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val products: List<Product> = emptyList(),
    val cartCount: Int = 0,
    /** Parent strip vs leaf strip after tapping a group. */
    val showingSubcategories: Boolean = false
) {
    val selectedGroup: CategoryGroup?
        get() = groups.firstOrNull { it.id == selectedGroupId }

    val subcategories: List<ProductCategory>
        get() = selectedGroup?.children.orEmpty()

    val selectedCategory: ProductCategory?
        get() = subcategories.firstOrNull { it.id == selectedCategoryId }

    val sectionTitle: String
        get() = when {
            showingSubcategories -> selectedCategory?.name ?: selectedGroup?.name.orEmpty()
            else -> selectedGroup?.name ?: "Products"
        }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val catalogRepository: CatalogRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val selectedGroupId = MutableStateFlow<String?>(null)
    private val selectedCategoryId = MutableStateFlow<String?>(null)
    private val showingSubcategories = MutableStateFlow(false)
    private val searchQuery = MutableStateFlow("")

    private val selection = combine(
        catalogRepository.observeGroups(),
        selectedGroupId,
        selectedCategoryId,
        showingSubcategories,
        searchQuery
    ) { groups, groupId, categoryId, drilled, query ->
        val effectiveGroup = groups.firstOrNull { it.id == groupId } ?: groups.firstOrNull()
        val effectiveCategoryId = when {
            drilled -> categoryId ?: effectiveGroup?.children?.firstOrNull()?.id
            else -> null
        }
        SelectionSnapshot(
            groups = groups,
            groupId = effectiveGroup?.id,
            categoryId = effectiveCategoryId,
            drilled = drilled,
            query = query,
            groupLeafIds = effectiveGroup?.children?.map { it.id }.orEmpty()
        )
    }

    private val products = selection.flatMapLatest { snap ->
        if (snap.drilled) {
            catalogRepository.observeProducts(snap.categoryId, snap.query)
        } else {
            catalogRepository.observeProducts(categoryId = null, query = snap.query).map { list ->
                if (snap.groupLeafIds.isEmpty()) list
                else list.filter { it.categoryId in snap.groupLeafIds }
            }
        }
    }

    val uiState: StateFlow<BrowseUiState> = combine(
        selection,
        products,
        cartRepository.itemCount
    ) { snap, productList, cartCount ->
        BrowseUiState(
            groups = snap.groups,
            selectedGroupId = snap.groupId,
            selectedCategoryId = snap.categoryId
                ?: snap.groups.firstOrNull { it.id == snap.groupId }
                    ?.children?.firstOrNull()?.id,
            searchQuery = snap.query,
            products = productList,
            cartCount = cartCount,
            showingSubcategories = snap.drilled
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BrowseUiState()
    )

    init {
        viewModelScope.launch {
            catalogRepository.observeGroups().collect { groups ->
                if (selectedGroupId.value == null && groups.isNotEmpty()) {
                    selectedGroupId.value = groups.first().id
                }
            }
        }
    }

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    fun onGroupSelected(groupId: String) {
        selectedGroupId.value = groupId
        val children = uiState.value.groups.firstOrNull { it.id == groupId }?.children
            ?: emptyList()
        selectedCategoryId.value = children.firstOrNull()?.id
        showingSubcategories.value = true
    }

    fun onCategorySelected(categoryId: String) {
        selectedCategoryId.value = categoryId
        showingSubcategories.value = true
    }

    fun onBackToGroups() {
        showingSubcategories.value = false
    }

    fun onAddToCart(productId: String) {
        viewModelScope.launch {
            cartRepository.addProduct(productId)
        }
    }

    private data class SelectionSnapshot(
        val groups: List<CategoryGroup>,
        val groupId: String?,
        val categoryId: String?,
        val drilled: Boolean,
        val query: String,
        val groupLeafIds: List<String>
    )
}
