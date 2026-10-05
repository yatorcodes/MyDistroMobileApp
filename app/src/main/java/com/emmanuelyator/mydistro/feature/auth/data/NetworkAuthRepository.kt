package com.emmanuelyator.mydistro.feature.auth.data

import com.emmanuelyator.mydistro.core.common.AppError
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.model.Driver
import com.emmanuelyator.mydistro.core.model.UserRole
import com.emmanuelyator.mydistro.core.network.TokenStore
import com.emmanuelyator.mydistro.feature.auth.domain.AuthRepository
import com.emmanuelyator.mydistro.feature.auth.domain.AuthSession
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class NetworkAuthRepository @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenStore: TokenStore
) : AuthRepository {

    private val _session = MutableStateFlow<AuthSession?>(null)
    override val session: StateFlow<AuthSession?> = _session.asStateFlow()

    init {
        // Hydrate session if token exists
        // Note: For a robust implementation, we should also store the user details,
        // or fetch them with a /me endpoint using the token.
        // For now, we will do a simple check. If we need the real details, we would decode the JWT.
        val savedToken = tokenStore.accessToken()
        if (savedToken != null) {
            // Ideally we parse the JWT here, or load cached user details.
            // For now, we assume if we have a token, we are signed in, but we might lack details.
        }
    }

    override suspend fun loginDriver(
        phoneOrEmail: String,
        password: String
    ): DataResult<AuthSession> = performLogin(phoneOrEmail, password, UserRole.DRIVER)

    override suspend fun loginCustomer(
        phoneOrEmail: String,
        password: String
    ): DataResult<AuthSession> = performLogin(phoneOrEmail, password, UserRole.CUSTOMER)

    private suspend fun performLogin(
        phoneOrEmail: String,
        password: String,
        expectedRole: UserRole
    ): DataResult<AuthSession> {
        return try {
            val response = apiService.login(LoginRequest(phoneOrEmail, password))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                
                // Save the token so AuthInterceptor uses it!
                tokenStore.save(body.accessToken)

                // Optional: parse role from string if backend sends it as string
                val parsedRole = try {
                    UserRole.valueOf(body.user.role.uppercase())
                } catch (e: Exception) {
                    expectedRole
                }

                val session = AuthSession(
                    userId = body.user.id,
                    role = parsedRole,
                    displayName = body.user.fullName
                )
                _session.value = session
                DataResult.Success(session)
            } else if (response.code() == 401 || response.code() == 403) {
                DataResult.Failure(AppError.InvalidCredentials())
            } else {
                DataResult.Failure(AppError.Network("Server returned ${response.code()}"))
            }
        } catch (e: Exception) {
            DataResult.Failure(AppError.Network(e.message ?: "Connection failed"))
        }
    }

    override suspend fun currentDriver(): DataResult<Driver> {
        val currentSession = _session.value
        if (currentSession == null || currentSession.role != UserRole.DRIVER) {
            return DataResult.Failure(AppError.Unauthorized)
        }
        
        return DataResult.Success(
            Driver(
                id = currentSession.userId,
                fullName = currentSession.displayName,
                phoneNumber = "0700000000",
                vehicleRegistration = "KXX 123X"
            )
        )
    }

    override suspend fun logout() {
        tokenStore.clear()
        _session.value = null
    }
}
