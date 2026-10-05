package com.emmanuelyator.mydistro.feature.distributor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroPrimaryButton
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

@Composable
fun DistributorHomeRoute(
    onNavigateToUpload: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            MyDistroTopBar(
                title = "Distributor Portal",
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.screenPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Computer,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MyDistroTheme.colors.textSecondary
            )
            
            Spacer(modifier = Modifier.height(Spacing.xl))
            
            Text(
                text = "Web Console Required",
                style = MaterialTheme.typography.titleLarge,
                color = MyDistroTheme.colors.textPrimary
            )
            
            Spacer(modifier = Modifier.height(Spacing.sm))
            
            Text(
                text = "Please use the MyDistro Web Console on your computer to manage orders, track stock, and view analytics.",
                style = MaterialTheme.typography.bodyLarge,
                color = MyDistroTheme.colors.textSecondary,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(Spacing.xxl))
            
            MyDistroPrimaryButton(
                text = "Upload Product Photos",
                leadingIcon = Icons.Outlined.PhotoCamera,
                onClick = onNavigateToUpload,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
