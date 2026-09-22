package com.emmanuelyator.mydistro.core.network

import java.net.HttpURLConnection
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Attaches the stored JWT to outgoing requests and reacts to rejection.
 *
 * On a 401 the token is dropped and [AuthEventBus] is notified so the app can
 * send the user back to login, rather than leaving every screen to discover the
 * dead session on its own.
 *
 * No refresh-token round trip yet — that needs the real Spring Boot contract.
 * Until then this is plumbing, not working authentication.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val authEventBus: AuthEventBus
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenStore.accessToken()

        val request = if (token != null && !original.skipsAuth()) {
            original.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }

        val response = chain.proceed(request)

        if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
            tokenStore.clear()
            authEventBus.publish(AuthEvent.SessionExpired)
        }

        return response
    }

    /** Login and password-setup calls must not carry a stale token. */
    private fun okhttp3.Request.skipsAuth(): Boolean =
        url.encodedPath.contains("/auth/")
}
