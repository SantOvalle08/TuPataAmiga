package co.edu.uniqundio.tupataamiga.features.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.usecase.user.RegisterUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nombre: String) {
        _uiState.update { state ->
            state.copy(
                nombre = nombre,
                nombreError = if (nombre.isBlank()) "El nombre es obligatorio" else null
            )
        }
    }

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

    fun onConfirmarClaveChanged(confirmarClave: String) {
        _uiState.update { state ->
            val error = when {
                confirmarClave.isBlank() -> "Confirma tu contraseña"
                confirmarClave != state.clave -> "Las contraseñas no coinciden"
                else -> null
            }
            state.copy(
                confirmarClave = confirmarClave,
                confirmarClaveError = error
            )
        }
    }

    fun onDireccionChanged(direccion: String) {
        _uiState.update { state ->
            state.copy(direccion = direccion)
        }
    }

    fun register(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        val nameErr = if (state.nombre.isBlank()) "El nombre es obligatorio" else null
        val emailErr = validateEmail(state.correo)
        val passErr = validatePassword(state.clave)
        val confirmPassErr = when {
            state.confirmarClave.isBlank() -> "Confirma tu contraseña"
            state.confirmarClave != state.clave -> "Las contraseñas no coinciden"
            else -> null
        }

        if (nameErr != null || emailErr != null || passErr != null || confirmPassErr != null) {
            _uiState.update {
                it.copy(
                    nombreError = nameErr,
                    correoError = emailErr,
                    claveError = passErr,
                    confirmarClaveError = confirmPassErr
                )
            }
            onError("Por favor corrige los campos señalados")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val usuario = Usuario(
                id = UUID.randomUUID().toString(),
                nombre = state.nombre,
                correo = state.correo,
                rol = Rol.USUARIO
            )
            val success = registerUserUseCase(usuario, state.clave)
            _uiState.update { it.copy(isLoading = false) }
            if (success) {
                onSuccess()
            } else {
                onError("El correo ya se encuentra registrado")
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

data class RegisterUiState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val confirmarClave: String = "",
    val direccion: String = "Armenia, Quindío",
    val nombreError: String? = null,
    val correoError: String? = null,
    val claveError: String? = null,
    val confirmarClaveError: String? = null,
    val isLoading: Boolean = false
)
