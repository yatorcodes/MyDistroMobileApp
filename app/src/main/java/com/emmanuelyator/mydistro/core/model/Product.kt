package com.emmanuelyator.mydistro.core.model

/**
 * Two-level catalogue: a parent group (e.g. Construction Materials) contains
 * leaf categories (Cement, Steel, Paint). Products always hang off a leaf.
 */
data class CategoryGroup(
    val id: String,
    val name: String,
    val children: List<ProductCategory>
)

data class ProductCategory(
    val id: String,
    val name: String,
    val groupId: String
)

data class Product(
    val id: String,
    val name: String,
    val brand: String,
    val categoryId: String,
    /** Display unit, e.g. "50kg", "20L", "1kg". */
    val unitLabel: String,
    /** Price in Kenyan shillings (whole units for the prototype). */
    val priceKes: Int,
    val inStock: Boolean = true
) {
    val priceLabel: String get() = "KSh %,d".format(priceKes)
}
