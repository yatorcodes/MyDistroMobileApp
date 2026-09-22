package com.emmanuelyator.mydistro.core.designsystem.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.emmanuelyator.mydistro.core.designsystem.theme.Dimens
import com.emmanuelyator.mydistro.core.designsystem.theme.MyDistroTheme
import com.emmanuelyator.mydistro.core.designsystem.theme.Spacing

/**
 * Segmented code entry.
 *
 * One real [BasicTextField] sits behind the boxes, which are drawn purely for
 * presentation. Six separate fields would break paste, backspace across
 * boundaries, and SMS autofill, so this keeps a single text value and renders it
 * split — the caller just receives a plain digit string.
 */
@Composable
fun OtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    enabled: Boolean = true,
    isError: Boolean = false,
    onDone: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val focused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = { raw ->
            // Digits only, capped at the code length: the visual boxes cannot
            // represent anything else, so reject it at the source.
            val digits = raw.filter(Char::isDigit).take(length)
            if (digits != value) onValueChange(digits)
        },
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                contentDescription =
                    "Delivery code, $length digits. ${value.length} of $length entered."
            },
        enabled = enabled,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        interactionSource = interactionSource,
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                repeat(length) { index ->
                    OtpCell(
                        char = value.getOrNull(index)?.toString().orEmpty(),
                        // Only one cell is ever "active": the next empty one.
                        active = focused && index == value.length.coerceAtMost(length - 1),
                        isError = isError,
                        enabled = enabled,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    )
}

@Composable
private fun OtpCell(
    char: String,
    active: Boolean,
    isError: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            active -> MaterialTheme.colorScheme.primary
            char.isNotEmpty() -> MyDistroTheme.colors.borderStrong
            else -> MyDistroTheme.colors.border
        },
        label = "otpCellBorder"
    )

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(MaterialTheme.shapes.extraSmall)
            .background(
                if (enabled) {
                    MaterialTheme.colorScheme.surface
                } else {
                    MyDistroTheme.colors.neutralContainer
                }
            )
            .border(
                width = if (active || isError) 2.dp else Dimens.hairline,
                color = borderColor,
                shape = MaterialTheme.shapes.extraSmall
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char,
            style = MaterialTheme.typography.headlineSmall,
            color = MyDistroTheme.colors.textPrimary
        )
    }
}
