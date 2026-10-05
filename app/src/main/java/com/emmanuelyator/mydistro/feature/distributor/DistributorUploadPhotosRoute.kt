package com.emmanuelyator.mydistro.feature.distributor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroCard
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing
import com.emmanuelyator.mydistro.core.model.Product
import com.emmanuelyator.mydistro.feature.customer.data.MockCatalogData
import kotlinx.coroutines.launch

@Composable
fun DistributorUploadPhotosRoute(
    onBack: () -> Unit
) {
    DistributorUploadPhotosScreen(
        products = MockCatalogData.products,
        onBack = onBack
    )
}

@Composable
fun DistributorUploadPhotosScreen(
    products: List<Product>,
    onBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    // Store temporarily overridden URIs for the demo
    val uploadedPhotos = remember { mutableStateMapOf<String, Uri>() }
    
    // Track which product is currently picking a photo
    var pickingForProductId: String? by remember { androidx.compose.runtime.mutableStateOf(null) }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null && pickingForProductId != null) {
                uploadedPhotos[pickingForProductId!!] = uri
                scope.launch {
                    snackbarHostState.showSnackbar("Photo uploaded successfully!")
                }
            }
            pickingForProductId = null
        }
    )

    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Upload Photos",
                onBack = onBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(Dimens.screenPadding),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            item {
                Text(
                    text = "Product Catalog",
                    style = MaterialTheme.typography.titleMedium,
                    color = MyDistroTheme.colors.textPrimary,
                    modifier = Modifier.padding(bottom = Spacing.xs)
                )
                Text(
                    text = "Tap upload to replace the current product image directly from your phone gallery.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MyDistroTheme.colors.textSecondary,
                    modifier = Modifier.padding(bottom = Spacing.md)
                )
            }
            
            items(products) { product ->
                val imageUri = uploadedPhotos[product.id] ?: product.imageUrl
                
                ProductUploadCard(
                    product = product,
                    currentImageUri = imageUri,
                    onUploadClick = {
                        pickingForProductId = product.id
                        photoPickerLauncher.launch(
                            androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductUploadCard(
    product: Product,
    currentImageUri: Any?,
    onUploadClick: () -> Unit
) {
    MyDistroCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = Spacing.sm
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = MyDistroTheme.colors.neutralContainer
            ) {
                if (currentImageUri != null) {
                    AsyncImage(
                        model = currentImageUri,
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.PhotoCamera,
                            contentDescription = null,
                            tint = MyDistroTheme.colors.textTertiary
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(Spacing.md))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MyDistroTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${product.brand} • ${product.unitLabel} • ${product.priceLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MyDistroTheme.colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(Spacing.xs))
                
                OutlinedButton(
                    onClick = onUploadClick,
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PhotoCamera,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
