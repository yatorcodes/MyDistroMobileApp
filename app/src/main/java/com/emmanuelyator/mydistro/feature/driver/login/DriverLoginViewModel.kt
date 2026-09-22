package com.emmanuelyator.mydistro.feature.driver.login

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

/**
 * Everything the login screen renders. Field-level errors are separate from
 * [formError] so a bad password highlights the field while a network failure
 * shows a banner.
 */
data class DriverLoginUiState(
    val identifier: String = "",
    val password: String = "",
    val identifierError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val isSubmitting: Boolean = false
) {
    /** Cheap gate for the button; full validation still runs on submit. */
    val canSubmit: Boolean
        get() = identifier.isNotBlank() && password.isNotBlank() && !isSubmitting
}

/** One-off effects. Modelled as events so they cannot be replayed on rotation. */
sealed interface DriverLoginEvent {
    data object LoginSucceeded : DriverLoginEvent
}

@HiltViewModel
class DriverLoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverLoginUiState())
    val uiState: StateFlow<DriverLoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<DriverLoginEvent>(Channel.BUFFERED)
    val events: Flow<DriverLoginEvent> = _events.receiveAsFlow()

    fun onIdentifierChange(value: String) {
        // Clearing errors as the user types avoids nagging them mid-correction.
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

    fun onSubmit() {
        val current = _uiState.value
        // Guard against a double tap landing two requests.
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
            when (val result = authRepository.loginDriver(current.identifier, current.password)) {
                is DataResult.Success -> {
                    // Leave isSubmitting true: the screen is navigating away and
                    // re-enabling the button would allow a second submission.
                    _events.send(DriverLoginEvent.LoginSucceeded)
                }

                // Reported as a form-level error, not a field error: we cannot
                // tell the user which of the two credentials was wrong, and
                // saying so would help credential stuffing.
                is DataResult.Failure -> _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    formError = result.error.message
                )
            }
        }
    }

    fun onFormErrorShown() {
        _uiState.value = _uiState.value.copy(formError = null)
    }
}
