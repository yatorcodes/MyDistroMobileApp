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
        Product("c0000000-0000-0000-0000-000000000001", "Bamburi Nguvu", "Bamburi", "cat-cement", "50kg", 750, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_bamburi_nguvu)),
        Product("c0000000-0000-0000-0000-000000000002", "Bamburi Tembo", "Bamburi", "cat-cement", "50kg", 770, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_bamburi_tembo)),
        Product("c0000000-0000-0000-0000-000000000003", "Bamburi Fundi", "Bamburi", "cat-cement", "50kg", 700, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_bamburi_fundi)),
        Product("ddad51d9-27c3-43d1-b20a-1708038b8b6f", "Bamburi Powerplus", "Bamburi", "cat-cement", "50kg", 850, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_bamburi_powerplus)),
        Product("5da2ead0-d3d7-451c-882f-636c726745e0", "Bamburi Powermax", "Bamburi", "cat-cement", "50kg", 900, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_bamburi_powermax)),
        Product("e9383398-aa95-485c-9769-be0ce33bab6e", "Simba Cement", "Simba", "cat-cement", "50kg", 740, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_simba_cement)),
        // Steel
        Product("ed648cf6-9505-4b11-8fcd-49b92d987977", "Steel Rods D12", "Devki", "cat-steel", "12m", 1_250, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_steel_rods)),
        Product("0e44d42f-729a-4d85-9c58-bf65ec71bf66", "Mabati Gauge 30", "Mabati Rolling", "cat-steel", "sheet", 720, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_mabati_gauge30)),
        // Paint
        Product("ea7839a6-28d7-4d41-883b-48ac34e42691", "Crown Silk Vinyl Emulsion", "Crown", "cat-paint", "20L", 4_800, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_crown_silk_vinyl_emulsion_20l)),
        Product("1cdaadf8-f438-4aee-9d30-13eca7deb818", "Basco Super Gloss", "Basco", "cat-paint", "20L", 5_200, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_basco_super_gloss)),
        // Maize & Rice
        Product("d793cd02-ca0d-4453-8aeb-e6159e1f8282", "Dry Maize", "Local Millers", "cat-maize", "90kg", 3_600, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_dry_maize)),
        Product("9330b5cf-0058-4b38-a7f5-4b0f62eeac09", "Pishori Rice", "Local Millers", "cat-rice", "25kg", 3_900, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_rice)),
        // Flour
        Product("6bcd5c24-fee7-4227-b2f3-e291f9289439", "EXE All Purpose Wheat Flour", "EXE", "cat-flour", "Wholesale Bale (2kgx12)", 2_150, listOf(
            "android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_exe_all_purpose_wheat_flour_wholesale_bag,
            "android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_exe_all_purpose_wheat_flour_2kgpackets
        )),
        // Oils
        Product("6bacae7f-63b9-45b6-a9c7-ffca7faf241d", "Fresh Fri Cooking Oil", "Fresh Fri", "cat-veg-oil", "20L", 4_200, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_freshfri_20l)),
        Product("1e35b7a5-33ba-4609-bfbb-e76a5f2c9a45", "Fresh Fri Cooking Oil", "Fresh Fri", "cat-veg-oil", "5L", 1_250, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_freshfri_5l)),
        Product("807d0b14-341a-4e3a-82cb-a1e59ce6a135", "Kimbo Cooking Fat", "Kimbo", "cat-palm-oil", "2kg", 520, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_kimbo_cooking_fat)),
        // Soaps
        Product("c963f35a-c6da-47e8-abf5-1f64bf4062b1", "Omo Washing Powder", "Omo", "cat-laundry", "1kg", 340, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_omo_washing_powder)),
        Product("1af00c2f-91cd-40ad-9716-67ab482e9461", "Sunlight Bar Soap", "Sunlight", "cat-laundry", "800g", 180, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_sunlight_bar_soap)),
        Product("ac6f907d-a0a9-450b-845a-493482eb4539", "Geisha Bath Soap", "Geisha", "cat-bath", "175g", 95, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_geisha_bath_soap)),
        Product("c0fafba2-f149-48bc-8f72-bde35d698934", "Vim Dishwashing Paste", "Vim", "cat-dish", "500g", 160, listOf("android.resource://com.emmanuelyator.mydistro/" + com.emmanuelyator.mydistro.R.drawable.img_vim_dishwashing_soap))
    )
}
