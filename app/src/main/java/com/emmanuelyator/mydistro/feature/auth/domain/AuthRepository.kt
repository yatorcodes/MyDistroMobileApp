package com.emmanuelyator.mydistro.feature.auth.domain

import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.Driver
import com.emmanuelyator.mydistro.core.model.UserRole
import kotlinx.coroutines.flow.StateFlow

/**
 * The signed-in user, as far as the app is concerned.
 *
 * Deliberately does not expose the raw token to the UI layer — the token goes
 * straight into [com.emmanuelyator.mydistro.core.network.TokenStore] and only
 * the interceptor reads it.
 */
data class AuthSession(
    val userId: String,
    val role: UserRole,
    val displayName: String
)

/**
 * Authentication boundary. Implementations decide whether credentials are
 * checked locally (prototype) or against the Spring Boot backend (production);
 * neither the ViewModels nor the Composables can tell the difference.
 */
interface AuthRepository {

    /** Null when nobody is signed in. Drives the app's start destination. */
    val session: StateFlow<AuthSession?>

    /**
     * Drivers are provisioned by administrators, so there is no signup — only
     * sign-in, and later a first-time password setup flow.
     */
    suspend fun loginDriver(phoneOrEmail: String, password: String): DataResult<AuthSession>

    /** Customers can self-register; login uses the same credential formats. */
    suspend fun loginCustomer(phoneOrEmail: String, password: String): DataResult<AuthSession>

    /** Profile of the signed-in driver, for the dashboard header. */
    suspend fun currentDriver(): DataResult<Driver>

    suspend fun logout()
}
