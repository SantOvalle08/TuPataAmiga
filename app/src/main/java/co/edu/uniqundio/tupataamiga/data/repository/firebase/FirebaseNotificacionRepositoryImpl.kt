package co.edu.uniqundio.tupataamiga.data.repository.firebase

import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotificacionRepositoryImpl @Inject constructor() : NotificacionRepository {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun observeNotificaciones(): Flow<List<Notificacion>> = callbackFlow {
        val listener = firestore.collection("notificaciones")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        runCatching {
                            doc.toObject(Notificacion::class.java)?.copy(id = doc.id)
                        }.getOrNull()
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun agregarNotificacion(notificacion: Notificacion) {
        runCatching {
            firestore.collection("notificaciones").document(notificacion.id).set(notificacion).await()
        }
    }

    override suspend fun marcarTodasLeidas() {
        runCatching {
            val snapshot = firestore.collection("notificaciones").get().await()
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.update(doc.reference, "leido", true)
            }
            batch.commit().await()
        }
    }
}
