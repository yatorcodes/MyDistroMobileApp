package com.emmanuelyator.mydistro.feature.customer.data

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.google.gson.JsonParser
import retrofit2.Response

/**
 * The backend returns one error shape everywhere: `{ code, message, ... }`.
 * Reads `message` so a customer sees "Insufficient available stock for X" or
 * "phoneNumber must be a valid Kenyan mobile number" instead of "HTTP 400".
 */
internal fun Response<*>.backendMessage(fallback: String): String {
    val raw = try {
        errorBody()?.string()
    } catch (_: Exception) {
        null
    }
    val message = raw?.let {
        runCatching { JsonParser.parseString(it).asJsonObject.get("message")?.asString }.getOrNull()
    }
    return message?.takeIf { it.isNotBlank() } ?: fallback
}

/** Maps a non-successful response to the [AppError] the UI shows. */
internal fun Response<*>.toFailure(fallback: String): DataResult.Failure {
    val error: AppError = when (code()) {
        401 -> AppError.Unauthorized
        in 500..599 -> AppError.Server()
        else -> AppError.Validation(backendMessage(fallback))
    }
    return DataResult.Failure(error)
}

internal fun Throwable.toConnectionFailure(): DataResult.Failure = DataResult.Failure(
    when (this) {
        is java.net.SocketTimeoutException -> AppError.Timeout
        is java.io.IOException -> AppError.NoConnection
        else -> AppError.Unknown(message ?: "Something went wrong. Please try again.")
    }
)
