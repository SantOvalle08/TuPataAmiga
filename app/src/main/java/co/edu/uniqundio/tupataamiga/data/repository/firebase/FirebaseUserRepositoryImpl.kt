package co.edu.uniqundio.tupataamiga.data.repository.firebase

import co.edu.uniqundio.tupataamiga.domain.model.Rol
import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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
    private val _lastError = MutableStateFlow<String?>(null)
    override val lastError: StateFlow<String?> = _lastError.asStateFlow()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                _currentUser.value = null
            } else {
                scope.launch { loadUser(firebaseUser.uid, firebaseUser.email.orEmpty(), firebaseUser.displayName) }
            }
        }
    }

    override suspend fun login(correo: String, clave: String): Usuario? {
        _lastError.value = null
        return try {
            val result = auth.signInWithEmailAndPassword(correo, clave).await()
            val firebaseUser = result.user ?: return null
            loadUser(firebaseUser.uid, correo, firebaseUser.displayName)
            _currentUser.value
        } catch (e: Exception) {
            _lastError.value = authErrorMessage(
                error = e,
                fallback = "No se pudo iniciar sesión. Inténtalo nuevamente.",
            )
            null
        }

    }

    override suspend fun getUserById(id: String): Usuario? {
        val document = firestore.collection("usuarios").document(id).get().await()
        val email = document.getString("correo").orEmpty()
        return Usuario(
            id = id,
            nombre = document.getString("nombre") ?: email.substringBefore("@"),
            correo = email,
            rol = runCatching { Rol.valueOf(document.getString("rol").orEmpty()) }
                .getOrDefault(Rol.USUARIO),
            direccion = document.getString("direccion").orEmpty(),
            fotoUrl = document.getString("fotoUrl").orEmpty(),
        )
    }

    override suspend fun register(usuario: Usuario, clave: String): Boolean {
        _lastError.value = null
        var createdUser: com.google.firebase.auth.FirebaseUser? = null
        return try {
            val result = auth.createUserWithEmailAndPassword(usuario.correo, clave).await()
            createdUser = result.user ?: error("Firebase no devolvió el usuario creado")
            val uid = createdUser.uid
            val nuevoUsuario = usuario.copy(id = uid)
            firestore.collection("usuarios").document(uid).set(nuevoUsuario).await()
            createdUser.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(usuario.nombre)
                    .setPhotoUri(usuario.fotoUrl.takeIf { it.isNotBlank() }?.let(android.net.Uri::parse))
                    .build(),
            ).await()
            _currentUser.value = nuevoUsuario
            true
        } catch (e: Exception) {
            if (createdUser != null) {
                runCatching { createdUser.delete().await() }
            }
            _lastError.value = registrationErrorMessage(e)
            Log.e("FirebaseUserRepository", "No se pudo registrar el usuario", e)
            false
        }
    }

    override suspend fun recuperarContrasena(correo: String): Boolean {
        _lastError.value = null
        return try {
            auth.sendPasswordResetEmail(correo).await()
            true
        } catch (e: Exception) {
            _lastError.value = authErrorMessage(
                error = e,
                fallback = "No se pudo enviar el correo de recuperación.",
            )
            false
        }
    }

    override fun setGuestSession() {
        _currentUser.value = null
    }

    override fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    private suspend fun loadUser(uid: String, email: String, displayName: String?): Usuario {
        val document = runCatching {
            firestore.collection("usuarios").document(uid).get().await()
        }.getOrNull()
        val storedRole = document?.getString("rol")
        val role = runCatching { Rol.valueOf(storedRole.orEmpty()) }
            .getOrDefault(if (email.lowercase().contains("admin")) Rol.MODERADOR else Rol.USUARIO)
        val user = Usuario(
            id = uid,
            nombre = document?.getString("nombre") ?: displayName ?: email.substringBefore("@"),
            correo = email,
            rol = role,
            direccion = document?.getString("direccion").orEmpty(),
            fotoUrl = document?.getString("fotoUrl").orEmpty(),
        )
        _currentUser.value = user
        return user
    }

    private fun authErrorMessage(error: Exception, fallback: String): String {
        val errorCode = (error as? FirebaseAuthException)?.errorCode
        return when (errorCode) {
            "ERROR_EMAIL_ALREADY_IN_USE" -> "Este correo ya está registrado."
            "ERROR_INVALID_EMAIL" -> "El correo no es válido."
            "ERROR_WEAK_PASSWORD" -> "La contraseña debe tener al menos 6 caracteres."
            "ERROR_NETWORK_REQUEST_FAILED" -> "No hay conexión con Firebase."
            "ERROR_WRONG_PASSWORD",
            "ERROR_INVALID_CREDENTIAL",
            "ERROR_INVALID_LOGIN_CREDENTIALS",
            "ERROR_USER_MISMATCH",
            "ERROR_USER_NOT_FOUND" -> "El correo o la contraseña son incorrectos."
            "ERROR_USER_DISABLED" -> "Esta cuenta está deshabilitada. Contacta al administrador."
            "ERROR_TOO_MANY_REQUESTS" -> "Demasiados intentos. Espera unos minutos y vuelve a intentarlo."
            "ERROR_REQUIRES_RECENT_LOGIN" -> "Por seguridad, vuelve a iniciar sesión e inténtalo otra vez."
            else -> {
                val message = error.message.orEmpty().lowercase()
                if (
                    "supplied auth credential" in message ||
                    "invalid credential" in message ||
                    "wrong password" in message ||
                    "password is invalid" in message
                ) {
                    "El correo o la contraseña son incorrectos."
                } else {
                    fallback
                }
            }
        }
    }

    private fun registrationErrorMessage(error: Exception): String {
        val authMessage = authErrorMessage(
            error = error,
            fallback = "",
        )
        if (authMessage.isNotBlank()) return authMessage
        return if (error is FirebaseAuthException) {
            "No se pudo crear la cuenta. Verifica los datos e inténtalo nuevamente."
        } else if (error is com.google.firebase.FirebaseException) {
            "No se pudo guardar el perfil en Firebase. Revisa tu conexión e inténtalo nuevamente."
        } else {
            "No se pudo completar el registro. Inténtalo nuevamente."
        }
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
