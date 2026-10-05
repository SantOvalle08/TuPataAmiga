package co.edu.uniqundio.tupataamiga.domain.usecase.user

import co.edu.uniqundio.tupataamiga.domain.model.Usuario
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(correo: String, clave: String): Usuario? =
        repository.login(correo, clave)
}
