package com.emmanuelyator.mydistro.feature.customer.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emmanuelyator.mydistro.core.common.DataResult
import com.emmanuelyator.mydistro.core.common.PasswordRules
import com.emmanuelyator.mydistro.core.common.Validators
import com.emmanuelyator.mydistro.feature.auth.domain.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class CustomerLoginUiState(
    val identifier: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val identifierError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val isSubmitting: Boolean = false
) {
    val canSubmit: Boolean
        get() = identifier.isNotBlank() && password.isNotBlank() && !isSubmitting
}

sealed interface CustomerLoginEvent {
    data object LoginSucceeded : CustomerLoginEvent
}

@HiltViewModel
class CustomerLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerLoginUiState())
    val uiState: StateFlow<CustomerLoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<CustomerLoginEvent>(Channel.BUFFERED)
    val events: Flow<CustomerLoginEvent> = _events.receiveAsFlow()

    fun onIdentifierChange(value: String) {
        _uiState.value = _uiState.value.copy(
            identifier = value,
            identifierError = null,
            formError = null
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            passwordError = null,
            formError = null
        )
    }

    fun onRememberMeChange(checked: Boolean) {
        // Prototype only — not persisted. Real remember-me needs EncryptedSharedPreferences.
        _uiState.value = _uiState.value.copy(rememberMe = checked)
    }

    fun onSubmit() {
        val current = _uiState.value
        if (current.isSubmitting) return

        val identifierError = when {
            current.identifier.isBlank() -> "Enter your phone number or email."
            !Validators.isValidPhoneOrEmail(current.identifier) ->
                "Enter a valid Kenyan phone number or email address."
            else -> null
        }
        val passwordError = PasswordRules.validate(current.password)

        if (identifierError != null || passwordError != null) {
            _uiState.value = current.copy(
                identifierError = identifierError,
                passwordError = passwordError
            )
            return
        }

        _uiState.value = current.copy(isSubmitting = true, formError = null)

        viewModelScope.launch {
            when (
                val result = authRepository.loginCustomer(current.identifier, current.password)
            ) {
                is DataResult.Success -> _events.send(CustomerLoginEvent.LoginSucceeded)
                is DataResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    formError = result.error.message
                )
            }
        }
    }
}
