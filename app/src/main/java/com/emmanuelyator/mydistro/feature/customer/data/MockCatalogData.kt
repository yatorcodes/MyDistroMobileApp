package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.model.CategoryGroup
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.core.model.ProductCategory

/**
 * Kenyan wholesale catalogue used until the Spring Boot products API exists.
 * Parent groups match how shopkeepers think ("construction", "cereals"), while
 * leaf categories match how stock is shelved.
 */
internal object MockCatalogData {

    val construction = CategoryGroup(
        id = "grp-construction",
        name = "Construction",
        children = listOf(
            ProductCategory("cat-cement", "Cement", "grp-construction"),
            ProductCategory("cat-steel", "Steel", "grp-construction"),
            ProductCategory("cat-paint", "Paint", "grp-construction")
        )
    )

    val cereals = CategoryGroup(
        id = "grp-cereals",
        name = "Cereals",
        children = listOf(
            ProductCategory("cat-maize", "Maize", "grp-cereals"),
            ProductCategory("cat-rice", "Rice", "grp-cereals"),
            ProductCategory("cat-flour", "Flour", "grp-cereals")
        )
    )

    val oils = CategoryGroup(
        id = "grp-oils",
        name = "Cooking Oil",
        children = listOf(
            ProductCategory("cat-veg-oil", "Vegetable", "grp-oils"),
            ProductCategory("cat-palm-oil", "Palm Oil", "grp-oils")
        )
    )

    val soaps = CategoryGroup(
        id = "grp-soaps",
        name = "Soaps",
        children = listOf(
            ProductCategory("cat-laundry", "Laundry", "grp-soaps"),
            ProductCategory("cat-bath", "Bath", "grp-soaps"),
            ProductCategory("cat-dish", "Dishwashing", "grp-soaps")
        )
    )

    val groups: List<CategoryGroup> = listOf(construction, cereals, oils, soaps)

    val products: List<Product> = listOf(
        // Cement
        Product("p-bamburi-50", "Bamburi Cement", "Bamburi", "cat-cement", "50kg", 580),
        Product("p-simba-50", "Simba Cement", "Simba", "cat-cement", "50kg", 560),
        Product("p-nyumba-50", "Nyumba Cement", "Nyumba", "cat-cement", "50kg", 550),
        Product("p-savannah-50", "Savannah Cement", "Savannah", "cat-cement", "50kg", 545),
        // Steel
        Product("p-devki-d12", "Steel Rods D12", "Devki", "cat-steel", "12m", 1_250),
        Product("p-devki-d10", "Steel Rods D10", "Devki", "cat-steel", "12m", 980),
        Product("p-mabati-gauge30", "Mabati Gauge 30", "Mabati Rolling", "cat-steel", "sheet", 720),
        // Paint
        Product("p-crown-20", "Crown Silk Emulsion", "Crown", "cat-paint", "20L", 4_800),
        Product("p-crown-4", "Crown Silk Emulsion", "Crown", "cat-paint", "4L", 1_150),
        Product("p-basco-20", "Basco Super Gloss", "Basco", "cat-paint", "20L", 5_200),
        // Maize
        Product("p-maize-90", "Dry Maize", "Local Millers", "cat-maize", "90kg", 3_600),
        Product("p-maize-50", "Dry Maize", "Local Millers", "cat-maize", "50kg", 2_050),
        // Rice
        Product("p-pembe-25", "Pembe Pishori", "Pembe", "cat-rice", "25kg", 3_900),
        Product("p-mwea-25", "Mwea Pishori", "Mwea", "cat-rice", "25kg", 4_100),
        // Flour
        Product("p-exemaize-2", "Exe Maize Flour", "Exe", "cat-flour", "2kg", 180),
        Product("p-soko-2", "Soko Maize Flour", "Soko", "cat-flour", "2kg", 175),
        // Oils
        Product("p-freshfry-5", "Fresh Fri Oil", "Fresh Fri", "cat-veg-oil", "5L", 1_450),
        Product("p-kimbo-2", "Kimbo Cooking Fat", "Kimbo", "cat-palm-oil", "2kg", 520),
        // Soaps
        Product("p-omo-1", "Omo Washing Powder", "Omo", "cat-laundry", "1kg", 340),
        Product("p-sunlight-800", "Sunlight Bar Soap", "Sunlight", "cat-laundry", "800g", 180),
        Product("p-geisha-175", "Geisha Bath Soap", "Geisha", "cat-bath", "175g", 95),
        Product("p-vim-500", "Vim Dishwashing Paste", "Vim", "cat-dish", "500g", 160)
    )
}
