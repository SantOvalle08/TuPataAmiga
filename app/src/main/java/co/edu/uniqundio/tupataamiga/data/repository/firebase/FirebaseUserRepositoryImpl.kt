package co.edu.uniqundio.tupataamiga.data.repository.firebase

import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseUserRepositoryImpl @Inject constructor() : UserRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val _currentUser = MutableStateFlow<Usuario?>(null)
    override val currentUser: StateFlow<Usuario?> = _currentUser.asStateFlow()

    init {
        runCatching {
            auth.addAuthStateListener { firebaseAuth ->
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser != null) {
                    val email = firebaseUser.email ?: ""
                    val isMod = email.lowercase().contains("admin")
                    _currentUser.value = Usuario(
                        id = firebaseUser.uid,
                        nombre = firebaseUser.displayName ?: email.substringBefore("@"),
                        correo = email,
                        rol = if (isMod) Rol.MODERADOR else Rol.USUARIO,
                    )
                } else {
                    _currentUser.value = null
                }
            }
        }
    }

    override suspend fun login(correo: String, clave: String): Usuario? {
        return try {
            val result = auth.signInWithEmailAndPassword(correo, clave).await()
            val firebaseUser = result.user ?: return null
            val isMod = correo.lowercase().contains("admin")
            val usuario = Usuario(
                id = firebaseUser.uid,
                nombre = firebaseUser.displayName ?: correo.substringBefore("@"),
                correo = correo,
                rol = if (isMod) Rol.MODERADOR else Rol.USUARIO,
            )
            _currentUser.value = usuario
            usuario
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun register(usuario: Usuario, clave: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(usuario.correo, clave).await()
            val uid = result.user?.uid ?: usuario.id
            val nuevoUsuario = usuario.copy(id = uid)
            firestore.collection("usuarios").document(uid).set(nuevoUsuario).await()
            _currentUser.value = nuevoUsuario
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun recuperarContrasena(correo: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(correo).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun setGuestSession() {
        _currentUser.value = null
    }

    override fun logout() {
        runCatching {
            auth.signOut()
        }
        _currentUser.value = null
    }

    override suspend fun updateUserProfile(nombre: String, correo: String): Boolean {
        val current = _currentUser.value ?: return false
        return try {
            val updated = current.copy(nombre = nombre, correo = correo)
            firestore.collection("usuarios").document(current.id).set(updated).await()
            _currentUser.value = updated
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteCurrentAccount(): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        return try {
            firestore.collection("usuarios").document(uid).delete().await()
            auth.currentUser?.delete()?.await()
            logout()
            true
        } catch (e: Exception) {
            false
        }
    }
}
