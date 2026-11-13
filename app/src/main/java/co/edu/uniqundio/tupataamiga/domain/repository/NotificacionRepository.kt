package co.edu.uniqundio.tupataamiga.domain.repository

import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import kotlinx.coroutines.flow.Flow

interface NotificacionRepository {
    fun observeNotificaciones(): Flow<List<Notificacion>>
    suspend fun agregarNotificacion(notificacion: Notificacion)
    suspend fun marcarTodasLeidas()
}
