package co.edu.uniqundio.tupataamiga.domain.usecase.user

import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(usuario: Usuario, clave: String): Boolean =
        repository.register(usuario, clave)
}
