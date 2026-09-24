package com.emmanuelyator.mydistro.core.network

import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
 * Persistent token storage using SharedPreferences for development prototyping.
 * Allows the user to stay signed in across app restarts.
 */
@Singleton
class PersistentTokenStore @Inject constructor(
    @ApplicationContext context: Context
) : TokenStore {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("mock_auth_prefs", Context.MODE_PRIVATE)

    override fun accessToken(): String? = prefs.getString("token", null)

    override fun save(accessToken: String) {
        prefs.edit().putString("token", accessToken).apply()
    }

    override fun clear() {
        prefs.edit().remove("token").apply()
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
    fun bindTokenStore(impl: PersistentTokenStore): TokenStore
}
