package co.edu.uniqundio.tupataamiga.features.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
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
        _uiState.update { it.copy(correo = correo) }
    }

    fun onClaveChanged(clave: String) {
        _uiState.update { it.copy(clave = clave) }
    }

    fun login(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val user = loginUserUseCase(_uiState.value.correo, _uiState.value.clave)
            _uiState.update { it.copy(isLoading = false) }
            if (user != null) {
                onSuccess()
            } else {
                onError("Credenciales inválidas")
            }
        }
    }
}

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val isLoading: Boolean = false
)
