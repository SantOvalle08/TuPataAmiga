package co.edu.uniqundio.tupataamiga.features.create

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.uniqundio.tupataamiga.data.remote.ai.AIService
import co.edu.uniqundio.tupataamiga.domain.model.*
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import co.edu.uniqundio.tupataamiga.domain.usecase.mascota.CreateMascotaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val createMascotaUseCase: CreateMascotaUseCase,
    private val aiService: AIService,
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState())
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    fun onNombreChanged(nombre: String) {
        _uiState.update { it.copy(nombre = nombre) }
    }

    fun onRazaChanged(raza: String) {
        _uiState.update { it.copy(raza = raza) }
    }

    fun onDescripcionChanged(descripcion: String) {
        _uiState.update { it.copy(descripcion = descripcion) }
    }

    fun generarDescripcionIa(onComplete: (String) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGeneratingIa = true) }
            val state = _uiState.value
            val sug = aiService.sugerirDescripcionMascota(
                tipoAnimal = state.tipoAnimal.name,
                raza = state.raza.ifBlank { "Mestizo" },
                tamano = state.tamano.name,
            )
            _uiState.update { it.copy(descripcion = sug, isGeneratingIa = false) }
            onComplete(sug)
        }
    }

    fun onCategoriaChanged(categoria: Categoria) {
        _uiState.update { it.copy(categoria = categoria) }
    }

    fun onTipoAnimalChanged(tipoAnimal: TipoAnimal) {
        _uiState.update { it.copy(tipoAnimal = tipoAnimal) }
    }

    fun onTamanoChanged(tamano: TamanoMascota) {
        _uiState.update { it.copy(tamano = tamano) }
    }

    fun onVacunasChanged(tieneVacunas: Boolean) {
        _uiState.update { it.copy(tieneVacunas = tieneVacunas) }
    }

    fun onUbicacionChanged(ubicacion: String) {
        if (ubicacion.isNotBlank()) {
            _uiState.update { it.copy(ubicacion = ubicacion.trim()) }
        }
    }

    fun onUbicacionSeleccionada(ubicacion: String, latitud: Double, longitud: Double) {
        _uiState.update {
            it.copy(
                ubicacion = ubicacion.trim(),
                latitud = latitud,
                longitud = longitud,
            )
        }
    }

    fun onNecesidadAdicionalChanged(necesidad: String) {
        _uiState.update { it.copy(necesidadAdicional = necesidad) }
    }

    fun onFotosAgregadas(nuevasUris: List<Uri>) {
        _uiState.update { state ->
            val combinadas = (state.fotosSeleccionadas + nuevasUris).distinct().take(5)
            state.copy(fotosSeleccionadas = combinadas)
        }
    }

    fun onFotoPrincipalSeleccionada(uri: Uri) {
        _uiState.update { it.copy(fotosSeleccionadas = listOf(uri)) }
    }

    fun onFotoEliminada(uri: Uri) {
        _uiState.update { state ->
            state.copy(fotosSeleccionadas = state.fotosSeleccionadas.filter { it != uri })
        }
    }

    private fun persistirImagenLocal(uri: Uri): String {
        return try {
            if (uri.scheme == "file") return uri.toString()
            val dir = File(context.filesDir, "mascotas").apply { mkdirs() }
            val destFile = File(dir, "mascota_${UUID.randomUUID()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(destFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            uri.toString()
        }
    }

    fun createMascota(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val state = _uiState.value

        if (state.nombre.isBlank()) {
            onError("Ingresa el título o nombre de la mascota")
            return
        }
        if (state.descripcion.isBlank()) {
            onError("Ingresa una descripción")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }

                val primeraFoto = state.fotosSeleccionadas.firstOrNull()
                val imagenFinal = primeraFoto?.let(::persistirImagenLocal).orEmpty()

                val mascota = Mascota(
                    id = UUID.randomUUID().toString(),
                    nombre = state.nombre,
                    categoria = state.categoria,
                    tipoAnimal = state.tipoAnimal,
                    raza = state.raza.ifBlank { "Mestizo" },
                    tamano = state.tamano,
                    vacunasAlDia = state.tieneVacunas,
                    vacunasDetalle = if (state.tieneVacunas) "Vacunas al día" else "Esquema en proceso",
                    nivelUrgencia = NivelUrgencia.MEDIO,
                    necesidadAdicional = state.necesidadAdicional,
                    estado = EstadoPublicacion.PENDIENTE,
                    descripcion = state.descripcion,
                    imagenUrl = imagenFinal,
                    ubicacion = state.ubicacion,
                    latitud = state.latitud,
                    longitud = state.longitud,
                    usuarioId = userRepository.currentUser.value?.id.orEmpty(),
                    nombrePublicante = userRepository.currentUser.value?.nombre ?: "Usuario PataAmiga",
                    fechaCreacion = "Hace un momento",
                )

                createMascotaUseCase(mascota)

                _uiState.value = CreateUiState()
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                onError(e.localizedMessage ?: "Ocurrió un error al guardar")
            }
        }
    }
}

data class CreateUiState(
    val nombre: String = "",
    val raza: String = "",
    val descripcion: String = "",
    val tamano: TamanoMascota = TamanoMascota.MEDIANO,
    val tieneVacunas: Boolean = true,
    val ubicacion: String = "El Bosque, Armenia",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val necesidadAdicional: String = "",
    val categoria: Categoria = Categoria.ADOPCION,
    val tipoAnimal: TipoAnimal = TipoAnimal.PERRO,
    val fotosSeleccionadas: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val isGeneratingIa: Boolean = false,
)
