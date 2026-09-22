package com.emmanuelyator.mydistro.feature.auth.data

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.common.Validators
import com.emmanuelyator.mydistro.core.model.Driver
import com.emmanuelyator.mydistro.core.model.UserRole
import com.emmanuelyator.mydistro.core.network.TokenStore
import com.emmanuelyator.mydistro.feature.auth.domain.AuthRepository
import com.emmanuelyator.mydistro.feature.auth.domain.AuthSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * PROTOTYPE ONLY — credentials are compared against constants in this file.
 *
 * Nothing here talks to a server, nothing is authenticated, and the "token" is a
 * fabricated string. This exists so the driver vertical slice can be built and
 * demoed before the Spring Boot API exists.
 *
 * Replacing it: implement [AuthRepository] over
 * [com.emmanuelyator.mydistro.core.network.ApiService] and change the single
 * `@Binds` in [com.emmanuelyator.mydistro.feature.auth.di.AuthModule]. No
 * ViewModel or Composable changes are required.
 */
@Singleton
class MockAuthRepository @Inject constructor(
    private val tokenStore: TokenStore
) : AuthRepository {

    private val _session = MutableStateFlow<AuthSession?>(null)
    override val session: StateFlow<AuthSession?> = _session.asStateFlow()

    override suspend fun loginDriver(
        phoneOrEmail: String,
        password: String
    ): DataResult<AuthSession> {
        // Stand in for network latency so loading states are actually exercised
        // during development rather than flashing past.
        delay(1_200)

        val identifier = phoneOrEmail.trim()
        val matchesPhone = Validators.toE164Kenya(identifier) == Validators.toE164Kenya(DEMO_PHONE)
        val matchesEmail = identifier.equals(DEMO_EMAIL, ignoreCase = true)

        if (!(matchesPhone || matchesEmail) || password != DEMO_PASSWORD) {
            return DataResult.Failure(AppError.InvalidCredentials())
        }

        tokenStore.save(FAKE_TOKEN)
        val session = AuthSession(
            userId = demoDriver.id,
            role = UserRole.DRIVER,
            displayName = demoDriver.fullName
        )
        _session.value = session
        return DataResult.Success(session)
    }

    override suspend fun currentDriver(): DataResult<Driver> {
        val active = _session.value ?: return DataResult.Failure(AppError.Unauthorized)
        return if (active.userId == demoDriver.id) {
            DataResult.Success(demoDriver)
        } else {
            DataResult.Failure(AppError.Unauthorized)
        }
    }

    override suspend fun logout() {
        tokenStore.clear()
        _session.value = null
    }

    private companion object {
        // Surfaced on the login screen as an explicitly labelled demo hint.
        const val DEMO_PHONE = "0712345678"
        const val DEMO_EMAIL = "alex.mwangi@mydistro.co.ke"
        const val DEMO_PASSWORD = "driver123"

        /** Not a JWT. Just proves the interceptor wiring works end to end. */
        const val FAKE_TOKEN = "mock-token-not-a-real-jwt"

        val demoDriver = Driver(
            id = "drv-001",
            fullName = "Alex Mwangi",
            phoneNumber = "+254712345678",
            vehicleRegistration = "KDA 421X"
        )
    }
}

/** Shown on the login screen so the demo credentials are never a secret. */
object DemoCredentials {
    const val PHONE = "0712345678"
    const val PASSWORD = "driver123"
}
