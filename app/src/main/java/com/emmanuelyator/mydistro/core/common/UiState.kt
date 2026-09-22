package com.emmanuelyator.mydistro.core.common

/**
 * The four states every data-backed screen has to handle. Empty is modelled
 * explicitly rather than as "success with an empty list" so screens cannot
 * forget to design for it.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data object Empty : UiState<Nothing>
    data class Error(val error: AppError) : UiState<Nothing>
}
