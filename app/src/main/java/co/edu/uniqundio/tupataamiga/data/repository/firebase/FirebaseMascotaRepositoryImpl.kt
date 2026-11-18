package co.edu.uniqundio.tupataamiga.data.repository.firebase

import co.edu.uniqundio.tupataamiga.domain.model.Comentario
import co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion
import co.edu.uniqundio.tupataamiga.domain.model.Mascota
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import co.edu.uniqundio.tupataamiga.data.remote.cloudinary.CloudinaryService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseMascotaRepositoryImpl @Inject constructor(
    private val cloudinaryService: CloudinaryService,
) : MascotaRepository {

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

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
        val docRef = firestore.collection("mascotas")
            .document(mascota.id.ifEmpty { UUID.randomUUID().toString() })
        val finalImageUrl = if (
            mascota.imagenUrl.startsWith("content://") ||
            mascota.imagenUrl.startsWith("file://")
        ) {
            cloudinaryService.uploadImage(android.net.Uri.parse(mascota.imagenUrl))
        } else {
            mascota.imagenUrl
        }

        val mascotaFinal = mascota.copy(id = docRef.id, imagenUrl = finalImageUrl)
        docRef.set(mascotaFinal).await()
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
        val userId = auth.currentUser?.uid ?: return
        val reference = firestore.collection("mascotas").document(mascotaId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(reference)
            val interesados = snapshot.get("interesadosIds") as? List<*> ?: emptyList<Any>()
            if (userId !in interesados) {
                transaction.update(
                    reference,
                    mapOf(
                        "interesadosIds" to com.google.firebase.firestore.FieldValue.arrayUnion(userId),
                        "interesAdopcionCount" to com.google.firebase.firestore.FieldValue.increment(1),
                    ),
                )
            }
            null
        }.await()
    }
}
