package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val usuario: Usuario? = null,
    val esInvitado: Boolean = true,
    val esModerador: Boolean = false,
    val nombreMostrar: String = "Usuario Invitado",
    val correoMostrar: String = "Modo lectura (Sin cuenta)",
    val rangoMostrar: String = "🐾 Invitado",
    val publicacionesContador: Int = 0,
    val comentariosContador: Int = 0,
    val puntosContador: Int = 0,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    mascotaRepository: MascotaRepository,
) : ViewModel() {

    val myPublications: StateFlow<List<co.edu.uniqundio.tupataamiga.domain.model.Mascota>> =
        combine(userRepository.currentUser, mascotaRepository.observeMascotas()) { user, mascotas ->
            if (user == null) emptyList() else mascotas.filter { it.usuarioId == user.id }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val adoptionInterests: StateFlow<List<co.edu.uniqundio.tupataamiga.domain.model.Mascota>> =
        combine(userRepository.currentUser, mascotaRepository.observeMascotas()) { user, mascotas ->
            if (user == null) emptyList() else mascotas.filter { user.id in it.interesadosIds }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.currentUser,
        mascotaRepository.observeMascotas(),
    ) { user, mascotas ->
            if (user == null) {
                ProfileUiState(
                    usuario = null,
                    esInvitado = true,
                    esModerador = false,
                    nombreMostrar = "Usuario Invitado",
                    correoMostrar = "Modo lectura (Sin cuenta)",
                    rangoMostrar = "🐾 Invitado",
                    publicacionesContador = 0,
                    comentariosContador = 0,
                    puntosContador = 0,
                )
            } else {
                val esMod = user.rol == Rol.MODERADOR
                val misMascotas = mascotas.filter { it.usuarioId == user.id }
                val comentarios = misMascotas.sumOf { it.comentarios.size }
                ProfileUiState(
                    usuario = user,
                    esInvitado = false,
                    esModerador = esMod,
                    nombreMostrar = user.nombre,
                    correoMostrar = user.correo,
                    rangoMostrar = if (esMod) "🛡️ Moderador" else "🐾 Amigo Animal",
                    publicacionesContador = if (esMod) mascotas.size else misMascotas.size,
                    comentariosContador = comentarios,
                    puntosContador = misMascotas.size * 20 + comentarios * 5,
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState(),
        )

    fun updateUserProfile(nombre: String, correo: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (uiState.value.esInvitado) {
                onResult(false, "Inicia sesión para editar tu perfil")
                return@launch
            }
            val success = userRepository.updateUserProfile(nombre, correo)
            if (success) {
                onResult(true, "Perfil actualizado correctamente")
            } else {
                onResult(false, "No se pudo actualizar el perfil")
            }
        }
    }

    fun logout() {
        userRepository.logout()
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userRepository.deleteCurrentAccount()
            onSuccess()
        }
    }
}
