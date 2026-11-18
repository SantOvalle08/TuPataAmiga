package co.edu.uniqundio.tupataamiga.data.repository.firebase

import co.edu.uniqundio.tupataamiga.domain.model.Notificacion
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseNotificacionRepositoryImpl @Inject constructor() : NotificacionRepository {

    private companion object {
        const val TAG = "FirebaseNotificacionRepo"
    }

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun observeNotificaciones(): Flow<List<Notificacion>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }
        val listener = firestore.collection("notificaciones")
            .whereEqualTo("usuarioId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            doc.toObject(Notificacion::class.java)?.copy(id = doc.id)
                        } catch (exception: Exception) {
                            Log.e(TAG, "No se pudo mapear la notificación ${doc.id}", exception)
                            null
                        }
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun agregarNotificacion(notificacion: Notificacion) {
        try {
            require(notificacion.usuarioId.isNotBlank()) {
                "No se puede crear una notificación sin usuarioId"
            }
            firestore.collection("notificaciones").document(notificacion.id).set(notificacion).await()
        } catch (exception: Exception) {
            Log.e(TAG, "No se pudo guardar la notificación ${notificacion.id}", exception)
            throw exception
        }
    }

    override suspend fun marcarTodasLeidas() {
        runCatching {
            val userId = auth.currentUser?.uid ?: return@runCatching
            val snapshot = firestore.collection("notificaciones")
                .whereEqualTo("usuarioId", userId)
                .get().await()
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.update(doc.reference, "leido", true)
            }
            batch.commit().await()
        }
    }
}
