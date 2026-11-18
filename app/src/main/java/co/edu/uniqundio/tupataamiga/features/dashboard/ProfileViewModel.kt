package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collect
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
    val interesesContador: Int = 0,
    val comentariosContador: Int = 0,
    val puntosContador: Int = 0,
    val activasContador: Int = 0,
    val verificadasContador: Int = 0,
    val pendientesContador: Int = 0,
    val finalizadasContador: Int = 0,
    val insigniasDesbloqueadas: Int = 0,
    val nivelActual: String = "Amigo Animal",
    val puntosSiguienteNivel: Int = 200,
    val progresoNivel: Float = 0f,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    mascotaRepository: MascotaRepository,
    private val notificacionRepository: NotificacionRepository,
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
                    interesesContador = 0,
                    comentariosContador = 0,
                    puntosContador = 0,
                )

            } else {
                val esMod = user.rol == Rol.MODERADOR
                val misMascotas = mascotas.filter { it.usuarioId == user.id }
                val comentarios = misMascotas.sumOf { it.comentarios.size }
                val intereses = mascotas.count { user.id in it.interesadosIds }
                val publicacionesAprobadas = misMascotas.count { it.estado == co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.APROBADO }
                val pendientes = misMascotas.count { it.estado == co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.PENDIENTE }
                val finalizadas = misMascotas.count { it.estado == co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.FINALIZADO }
                val puntos = misMascotas.size * 20 + comentarios * 5 + intereses * 10
                val nivel = when {
                    puntos >= 1000 -> "Héroe"
                    puntos >= 500 -> "Guardián"
                    puntos >= 200 -> "Protector"
                    else -> "Amigo Animal"
                }

                val siguiente = when {
                    puntos >= 1000 -> 1000
                    puntos >= 500 -> 1000
                    puntos >= 200 -> 500
                    else -> 200
                }
                val anterior = when {
                    puntos >= 1000 -> 1000
                    puntos >= 500 -> 500
                    puntos >= 200 -> 200
                    else -> 0
                }
                val insignias = listOf(
                    misMascotas.isNotEmpty(),
                    publicacionesAprobadas >= 5,
                    intereses >= 3,
                    false,
                    puntos >= 1000,
                    comentarios >= 50,
                ).count { it }
                ProfileUiState(
                    usuario = user,
                    esInvitado = false,
                    esModerador = esMod,
                    nombreMostrar = user.nombre,
                    correoMostrar = user.correo,
                    rangoMostrar = if (esMod) "🛡️ Moderador" else "🐾 $nivel",
                    publicacionesContador = if (esMod) mascotas.size else misMascotas.size,
                    interesesContador = intereses,
                    comentariosContador = comentarios,
                    puntosContador = puntos,
                    activasContador = publicacionesAprobadas,
                    verificadasContador = publicacionesAprobadas,
                    pendientesContador = pendientes,
                    finalizadasContador = finalizadas,
                    insigniasDesbloqueadas = insignias,
                    nivelActual = nivel,
                    puntosSiguienteNivel = siguiente,
                    progresoNivel = ((puntos - anterior).toFloat() / (siguiente - anterior).coerceAtLeast(1)).coerceIn(0f, 1f),
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState(),
        )

    init {
        viewModelScope.launch {
            uiState.collect { state ->
                val user = state.usuario ?: return@collect
                achievementDefinitions(state).forEach { achievement ->
                    if (achievement.unlocked) {
                        notificacionRepository.agregarNotificacion(
                            Notificacion(
                                id = "logro_${user.id}_${achievement.id}",
                                titulo = "¡Nuevo logro desbloqueado!",
                                cuerpo = "Obtuviste la insignia \"${achievement.name}\".",
                                tiempo = "Ahora",
                                tipo = "LOGRO",
                                usuarioId = user.id,
                            ),
                        )
                    }
                }
            }
        }
    }

    private fun achievementDefinitions(state: ProfileUiState): List<AchievementState> =
        listOf(
            AchievementState("primera_huella", "Primera Huella", state.publicacionesContador >= 1),
            AchievementState("rescatador", "Rescatador", state.verificadasContador >= 5),
            AchievementState("corazon_generoso", "Corazón Generoso", state.interesesContador >= 3),
            AchievementState("heroe_mascotas", "Héroe de Mascotas", state.puntosContador >= 1000),
            AchievementState("comentarista", "Comentarista", state.comentariosContador >= 50),
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

    private data class AchievementState(
        val id: String,
        val name: String,
        val unlocked: Boolean,
    )

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
