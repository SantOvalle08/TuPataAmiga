package co.edu.uniqundio.tupataamiga.features.home

import androidx.lifecycle.ViewModel
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    fun setGuestSession() {
        userRepository.setGuestSession()
    }
}
