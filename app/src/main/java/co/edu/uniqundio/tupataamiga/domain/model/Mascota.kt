package co.edu.uniqundio.tupataamiga.domain.model

import androidx.annotation.Keep

@Keep
data class Mascota(
    val id: String = "",
    val nombre: String = "",
    val categoria: Categoria = Categoria.ADOPCION,
    val tipoAnimal: TipoAnimal = TipoAnimal.PERRO,
    val raza: String = "Mestizo",
    val tamano: TamanoMascota = TamanoMascota.MEDIANO,
    val vacunasAlDia: Boolean = true,
    val vacunasDetalle: String = "Vacunas al día",
    val nivelUrgencia: NivelUrgencia = NivelUrgencia.MEDIO,
    val necesidadAdicional: String = "Apoyo en transporte / alimento",
    val estado: EstadoPublicacion = EstadoPublicacion.APROBADO,
    val descripcion: String = "",
    val imagenUrl: String = "",
    val ubicacion: String = "Armenia, Quindío",
    val latitud: Double? = null,
    val longitud: Double? = null,
    val usuarioId: String = "1",
    val nombrePublicante: String = "Usuario PataAmiga",
    val fechaCreacion: String = "Hoy",
    val comentarios: List<Comentario> = emptyList(),
    val interesAdopcionCount: Int = 0,
    val interesadosIds: List<String> = emptyList(),
    val motivoRechazo: String = ""
)
