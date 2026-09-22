package com.emmanuelyator.mydistro.core.common

/**
 * Repository-layer outcome. Kept separate from Kotlin's own [Result] so that
 * failures carry a message the UI can show without inspecting exception types.
 */
sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>
    data class Failure(val error: AppError) : DataResult<Nothing>
}

/**
 * Errors the UI knows how to render. Mapping from exceptions to these happens in
 * the data layer so no Composable or ViewModel has to know about OkHttp or Room.
 */
sealed interface AppError {
    val message: String

    data object NoConnection : AppError {
        override val message: String = "No internet connection. Check your network and try again."
    }

    data object Unauthorized : AppError {
        override val message: String = "Your session has expired. Please sign in again."
    }

    data object Timeout : AppError {
        override val message: String = "The request took too long. Please try again."
    }

    data class InvalidCredentials(
        override val message: String = "Incorrect phone number or password."
    ) : AppError

    data class Validation(override val message: String) : AppError

    data class Server(override val message: String = "Something went wrong on our side.") : AppError

    data class Unknown(
        override val message: String = "Something went wrong. Please try again."
    ) : AppError
}

inline fun <T, R> DataResult<T>.map(transform: (T) -> R): DataResult<R> = when (this) {
    is DataResult.Success -> DataResult.Success(transform(data))
    is DataResult.Failure -> this
}
