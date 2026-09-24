package com.emmanuelyator.mydistro.feature.customer.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material.icons.outlined.FormatPaint
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.OilBarrel
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Soap
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroLogo
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTextField
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.feature.customer.data.MockCatalogData

@Composable
fun CustomerHomeRoute(
    viewModel: CustomerHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CustomerHomeScreen(
        uiState = uiState,
        onSearchChange = viewModel::onSearchChange,
        onGroupSelected = viewModel::onGroupSelected,
        onCategorySelected = viewModel::onCategorySelected,
        onBackToGroups = viewModel::onBackToGroups,
        onAddToCart = viewModel::onAddToCart,
        onSeeAll = {
            // Drill into the group's first leaf if still at parent level.
            val state = uiState
            if (!state.showingSubcategories) {
                state.selectedGroupId?.let(viewModel::onGroupSelected)
            }
        }
    )
}

@Composable
fun CustomerHomeScreen(
    uiState: BrowseUiState,
    onSearchChange: (String) -> Unit,
    onGroupSelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onBackToGroups: () -> Unit,
    onAddToCart: (String) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    StatusBarIcons(dark = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            MyDistroLogo(
                modifier = Modifier.padding(
                    horizontal = Dimens.screenPadding,
                    vertical = Spacing.md
                ),
                markSize = 28.dp,
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            MyDistroTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                placeholder = "Search products...",
                leadingIcon = Icons.Outlined.Search,
                imeAction = ImeAction.Search,
                modifier = Modifier.padding(horizontal = Dimens.screenPadding)
            )

            Spacer(Modifier.height(Spacing.lg))

            CategoryStrip(
                uiState = uiState,
                onGroupSelected = onGroupSelected,
                onCategorySelected = onCategorySelected,
                onBackToGroups = onBackToGroups
            )

            Spacer(Modifier.height(Spacing.xl))

            SectionHeader(
                title = uiState.sectionTitle,
                onSeeAll = onSeeAll
            )

            Spacer(Modifier.height(Spacing.md))

            if (uiState.products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(Dimens.screenPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.searchQuery.isBlank()) {
                            "No products in this category yet."
                        } else {
                            "No products match “${uiState.searchQuery}”."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MyDistroTheme.colors.textSecondary
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = Dimens.screenPadding,
                        end = Dimens.screenPadding,
                        bottom = Spacing.xxl
                    ),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    items(uiState.products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onAdd = { onAddToCart(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryStrip(
    uiState: BrowseUiState,
    onGroupSelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onBackToGroups: () -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = Dimens.screenPadding),
        horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        if (uiState.showingSubcategories) {
            item(key = "back") {
                CategoryChip(
                    label = "All",
                    icon = Icons.AutoMirrored.Outlined.ArrowBack,
                    selected = false,
                    onClick = onBackToGroups
                )
            }
            items(uiState.subcategories, key = { it.id }) { category ->
                CategoryChip(
                    label = category.name,
                    icon = iconForCategory(category.id),
                    selected = category.id == uiState.selectedCategoryId,
                    onClick = { onCategorySelected(category.id) }
                )
            }
        } else {
            items(uiState.groups, key = { it.id }) { group ->
                CategoryChip(
                    label = group.name,
                    icon = iconForGroup(group.id),
                    selected = group.id == uiState.selectedGroupId,
                    onClick = { onGroupSelected(group.id) }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val container = if (selected) {
        MyDistroTheme.colors.heroSurface
    } else {
        MyDistroTheme.colors.neutralContainer
    }
    val content = if (selected) {
        MyDistroTheme.colors.onHeroSurface
    } else {
        MyDistroTheme.colors.textPrimary
    }
    val labelColor = if (selected) {
        MyDistroTheme.colors.textPrimary
    } else {
        MyDistroTheme.colors.textSecondary
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(72.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(container),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = content,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(Spacing.sm))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = labelColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    onSeeAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.screenPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MyDistroTheme.colors.textPrimary,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onSeeAll) {
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelLarge,
                color = MyDistroTheme.colors.textSecondary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = MyDistroTheme.colors.textSecondary,
                modifier = Modifier.size(Dimens.iconMd)
            )
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    MyDistroCard(
        modifier = modifier,
        contentPadding = Spacing.md
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .clip(MaterialTheme.shapes.small)
                .background(MyDistroTheme.colors.neutralContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconForCategory(product.categoryId),
                contentDescription = null,
                tint = MyDistroTheme.colors.textSecondary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = product.name,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MyDistroTheme.colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = product.unitLabel,
            style = MaterialTheme.typography.bodySmall,
            color = MyDistroTheme.colors.textTertiary
        )

        Spacer(Modifier.height(Spacing.xs))

        Text(
            text = product.priceLabel,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MyDistroTheme.colors.textPrimary
        )

        Spacer(Modifier.height(Spacing.sm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.brand,
                style = MaterialTheme.typography.labelMedium,
                color = MyDistroTheme.colors.success,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary)
                    .clickable(onClick = onAdd)
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            ) {
                Text(
                    text = "Add",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

private fun iconForGroup(groupId: String): ImageVector = when (groupId) {
    "grp-construction" -> Icons.Outlined.Construction
    "grp-cereals" -> Icons.Outlined.Agriculture
    "grp-oils" -> Icons.Outlined.OilBarrel
    "grp-soaps" -> Icons.Outlined.Soap
    else -> Icons.Outlined.Category
}

private fun iconForCategory(categoryId: String): ImageVector = when (categoryId) {
    "cat-cement" -> Icons.Outlined.Inventory2
    "cat-steel" -> Icons.Outlined.Straighten
    "cat-paint" -> Icons.Outlined.FormatPaint
    "cat-maize", "cat-rice", "cat-flour" -> Icons.Outlined.Agriculture
    "cat-veg-oil", "cat-palm-oil" -> Icons.Outlined.WaterDrop
    "cat-laundry", "cat-bath", "cat-dish" -> Icons.Outlined.Soap
    else -> Icons.Outlined.Category
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun CustomerHomePreview() {
    MyDistroTheme {
        CustomerHomeScreen(
            uiState = BrowseUiState(
                groups = MockCatalogData.groups,
                selectedGroupId = MockCatalogData.construction.id,
                selectedCategoryId = "cat-cement",
                products = MockCatalogData.products.filter { it.categoryId == "cat-cement" },
                showingSubcategories = true
            ),
            onSearchChange = {},
            onGroupSelected = {},
            onCategorySelected = {},
            onBackToGroups = {},
            onAddToCart = {},
            onSeeAll = {}
        )
    }
}
