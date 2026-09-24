package com.emmanuelyator.mydistro.feature.splash

import androidx.lifecycle.ViewModel
import com.emmanuelyator.mydistro.core.model.UserRole
import com.emmanuelyator.mydistro.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun getNextRoute(
        roleSelectionRoute: String,
        driverTripsRoute: String,
        customerHomeRoute: String
    ): String {
        // Safe to use runBlocking here because we only read a StateFlow's value synchronously.
        val session = authRepository.session.value
        return when (session?.role) {
            UserRole.DRIVER -> driverTripsRoute
            UserRole.CUSTOMER -> customerHomeRoute
            else -> roleSelectionRoute
        }
    }
}
