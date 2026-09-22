package com.emmanuelyator.mydistro.feature.placeholder

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Construction
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.emmanuelyator.mydistro.core.designsystem.component.EmptyState
import com.emmanuelyator.mydistro.core.designsystem.component.MyDistroTopBar
import com.emmanuelyator.mydistro.core.designsystem.theme.StatusBarIcons

/**
 * Honest placeholder for routes that navigation reaches but that later phases
 * will build — the driver's map, history and profile tabs, first-time password
 * setup, and the whole customer experience.
 *
 * Existing as a real destination means navigation can be tested end to end
 * without any screen pretending to have functionality it does not have.
 */
@Composable
fun ComingSoonScreen(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Outlined.Construction,
    onBack: (() -> Unit)? = null
) {
    StatusBarIcons(dark = true)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { MyDistroTopBar(title = title, onBack = onBack) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            EmptyState(title = title, description = description, icon = icon)
        }
    }
}
