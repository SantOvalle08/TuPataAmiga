package co.edu.uniqundio.tupataamiga.data.repository.firebase

import androidx.core.net.toUri
import co.edu.uniqundio.tupataamiga.domain.model.Comentario
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMascotaRepositoryImpl @Inject constructor() : MascotaRepository {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storage: FirebaseStorage by lazy { FirebaseStorage.getInstance() }

    override fun observeMascotas(): Flow<List<Mascota>> = callbackFlow {
        val listener = firestore.collection("mascotas")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        runCatching {
                            doc.toObject(Mascota::class.java)?.copy(id = doc.id)
                        }.getOrNull()
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getMascotaById(id: String): Mascota? {
        return runCatching {
            val doc = firestore.collection("mascotas").document(id).get().await()
            doc.toObject(Mascota::class.java)?.copy(id = doc.id)
        }.getOrNull()
    }

    override suspend fun createMascota(mascota: Mascota) {
        runCatching {
            val docRef = firestore.collection("mascotas").document(mascota.id.ifEmpty { UUID.randomUUID().toString() })
            var finalImageUrl = mascota.imagenUrl

            if (finalImageUrl.startsWith("content://") || finalImageUrl.startsWith("file://")) {
                runCatching {
                    val storageRef = storage.reference.child("mascotas/${docRef.id}.jpg")
                    storageRef.putFile(finalImageUrl.toUri()).await()
                    finalImageUrl = storageRef.downloadUrl.await().toString()
                }
            }

            val mascotaFinal = mascota.copy(id = docRef.id, imagenUrl = finalImageUrl)
            docRef.set(mascotaFinal).await()
        }
    }

    override suspend fun updateMascota(mascota: Mascota) {
        runCatching {
            firestore.collection("mascotas").document(mascota.id).set(mascota).await()
        }
    }

    override suspend fun deleteMascota(id: String) {
        runCatching {
            firestore.collection("mascotas").document(id).delete().await()
        }
    }

    override suspend fun aprobarMascota(id: String) {
        runCatching {
            firestore.collection("mascotas").document(id).update(
                mapOf(
                    "estado" to EstadoPublicacion.APROBADO.name,
                    "motivoRechazo" to "",
                ),
            ).await()
        }
    }

    override suspend fun rechazarMascota(id: String, motivo: String) {
        runCatching {
            firestore.collection("mascotas").document(id).update(
                mapOf(
                    "estado" to EstadoPublicacion.RECHAZADO.name,
                    "motivoRechazo" to motivo,
                ),
            ).await()
        }
    }

    override suspend fun finalizarMascota(id: String) {
        runCatching {
            firestore.collection("mascotas").document(id).update("estado", EstadoPublicacion.FINALIZADO.name).await()
        }
    }

    override suspend fun agregarComentario(mascotaId: String, comentario: Comentario) {
        runCatching {
            val mascota = getMascotaById(mascotaId) ?: return
            val listaActualizada = mascota.comentarios + comentario
            firestore.collection("mascotas").document(mascotaId).update("comentarios", listaActualizada).await()
        }
    }

    override suspend fun incrementarInteres(mascotaId: String) {
        runCatching {
            val mascota = getMascotaById(mascotaId) ?: return
            firestore.collection("mascotas").document(mascotaId).update("interesAdopcionCount", mascota.interesAdopcionCount + 1).await()
        }
    }
}
