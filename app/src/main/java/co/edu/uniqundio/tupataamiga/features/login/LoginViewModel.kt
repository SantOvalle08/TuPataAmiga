package co.edu.uniqundio.tupataamiga.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.usecase.user.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoChanged(correo: String) {
        _uiState.update { state ->
            state.copy(
                correo = correo,
                correoError = validateEmail(correo)
            )
        }
    }

    fun onClaveChanged(clave: String) {
        _uiState.update { state ->
            state.copy(
                clave = clave,
                claveError = validatePassword(clave)
            )
        }
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val emailErr = validateEmail(state.correo)
        val passErr = validatePassword(state.clave)

        if (emailErr != null || passErr != null) {
            _uiState.update {
                it.copy(correoError = emailErr, claveError = passErr)
            }
            onError("Revisa el correo y la contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = loginUserUseCase(state.correo, state.clave)
            _uiState.update { it.copy(isLoading = false) }
            if (user != null) {
                onSuccess()
            } else {
                onError("Credenciales inválidas o correo no registrado")
            }
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El correo es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un correo válido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es obligatoria"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }
    }
}

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val correoError: String? = null,
    val claveError: String? = null,
    val isLoading: Boolean = false
) {
    val isFormValid: Boolean
        get() = correo.isNotBlank() &&
                clave.isNotBlank() &&
                correoError == null &&
                claveError == null
}
