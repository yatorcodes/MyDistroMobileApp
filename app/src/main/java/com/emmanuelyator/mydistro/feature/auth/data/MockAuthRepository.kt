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
 * Nothing here talks to a server. Replacing it is a single Hilt `@Binds` change.
 */
@Singleton
class MockAuthRepository @Inject constructor(
    private val tokenStore: TokenStore
) : AuthRepository {

    private val _session = MutableStateFlow<AuthSession?>(null)
    override val session: StateFlow<AuthSession?> = _session.asStateFlow()

    init {
        val savedToken = tokenStore.accessToken()
        if (savedToken == FAKE_DRIVER_TOKEN) {
            _session.value = AuthSession(
                userId = demoDriver.id,
                role = UserRole.DRIVER,
                displayName = demoDriver.fullName
            )
        } else if (savedToken == FAKE_CUSTOMER_TOKEN) {
            _session.value = AuthSession(
                userId = "cust-001",
                role = UserRole.CUSTOMER,
                displayName = "Grace Wanjiku"
            )
        }
    }

    override suspend fun loginDriver(
        phoneOrEmail: String,
        password: String
    ): DataResult<AuthSession> {
        delay(1_200)
        return authenticate(
            phoneOrEmail = phoneOrEmail,
            password = password,
            expectedPhone = DemoCredentials.PHONE,
            expectedEmail = DemoCredentials.EMAIL,
            expectedPassword = DemoCredentials.PASSWORD,
            session = AuthSession(
                userId = demoDriver.id,
                role = UserRole.DRIVER,
                displayName = demoDriver.fullName
            ),
            token = FAKE_DRIVER_TOKEN
        )
    }

    override suspend fun loginCustomer(
        phoneOrEmail: String,
        password: String
    ): DataResult<AuthSession> {
        delay(1_200)
        return authenticate(
            phoneOrEmail = phoneOrEmail,
            password = password,
            expectedPhone = DemoCustomerCredentials.PHONE,
            expectedEmail = DemoCustomerCredentials.EMAIL,
            expectedPassword = DemoCustomerCredentials.PASSWORD,
            session = AuthSession(
                userId = "cust-001",
                role = UserRole.CUSTOMER,
                displayName = "Grace Wanjiku"
            ),
            token = FAKE_CUSTOMER_TOKEN
        )
    }

    override suspend fun currentDriver(): DataResult<Driver> {
        val active = _session.value ?: return DataResult.Failure(AppError.Unauthorized)
        return if (active.role == UserRole.DRIVER && active.userId == demoDriver.id) {
            DataResult.Success(demoDriver)
        } else {
            DataResult.Failure(AppError.Unauthorized)
        }
    }

    override suspend fun logout() {
        tokenStore.clear()
        _session.value = null
    }

    private fun authenticate(
        phoneOrEmail: String,
        password: String,
        expectedPhone: String,
        expectedEmail: String,
        expectedPassword: String,
        session: AuthSession,
        token: String
    ): DataResult<AuthSession> {
        val identifier = phoneOrEmail.trim()
        val matchesPhone =
            Validators.toE164Kenya(identifier) == Validators.toE164Kenya(expectedPhone)
        val matchesEmail = identifier.equals(expectedEmail, ignoreCase = true)

        if (!(matchesPhone || matchesEmail) || password != expectedPassword) {
            return DataResult.Failure(AppError.InvalidCredentials())
        }

        tokenStore.save(token)
        _session.value = session
        return DataResult.Success(session)
    }

    private companion object {
        const val FAKE_DRIVER_TOKEN = "mock-driver-token-not-a-real-jwt"
        const val FAKE_CUSTOMER_TOKEN = "mock-customer-token-not-a-real-jwt"

        val demoDriver = Driver(
            id = "drv-001",
            fullName = "Alex Mwangi",
            phoneNumber = "+254712345678",
            vehicleRegistration = "KDA 421X"
        )
    }
}

object DemoCredentials {
    const val PHONE = "0712345678"
    const val EMAIL = "alex.mwangi@mydistro.co.ke"
    const val PASSWORD = "driver123"
}

object DemoCustomerCredentials {
    const val PHONE = "0722001122"
    const val EMAIL = "grace@hardware.co.ke"
    const val PASSWORD = "customer123"
}
