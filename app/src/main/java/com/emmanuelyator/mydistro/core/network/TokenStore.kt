package com.emmanuelyator.mydistro.core.network

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Holds the JWT for outgoing requests.
 *
 * Deliberately an interface: the prototype uses an in-memory implementation, and
 * a persistent encrypted one can be bound in [TokenStoreModule] later without
 * touching the interceptor or any caller.
 */
interface TokenStore {
    fun accessToken(): String?
    fun save(accessToken: String)
    fun clear()
}

/**
 * In-memory token storage. Tokens are lost when the process dies, so the user
 * signs in again after a cold start — acceptable while the whole auth flow is
 * mocked, and safer than writing credentials to plain SharedPreferences.
 *
 * TODO: before shipping real auth, replace this binding with an implementation
 * backed by EncryptedSharedPreferences (androidx.security:security-crypto) or
 * DataStore with a Keystore-wrapped key, and add refresh-token handling.
 * This is NOT secure token storage yet.
 */
@Singleton
class InMemoryTokenStore @Inject constructor() : TokenStore {
    @Volatile
    private var token: String? = null

    override fun accessToken(): String? = token

    override fun save(accessToken: String) {
        token = accessToken
    }

    override fun clear() {
        token = null
    }
}

/**
 * Broadcasts auth-level network events so the navigation layer can react to an
 * expired session from anywhere, without every repository having to handle 401s.
 */
@Singleton
class AuthEventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AuthEvent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    fun publish(event: AuthEvent) {
        _events.tryEmit(event)
    }
}

sealed interface AuthEvent {
    /** The server rejected our token; the user has to sign in again. */
    data object SessionExpired : AuthEvent
}

@Module
@InstallIn(SingletonComponent::class)
interface TokenStoreModule {
    @Binds
    fun bindTokenStore(impl: InMemoryTokenStore): TokenStore
}
