package co.edu.uniqundio.tupataamiga.core

sealed interface RequestResult<out T> {
    data class Success<T>(val data: T) : RequestResult<T>
    data class Failure(val exception: Throwable) : RequestResult<Nothing>
    object Loading : RequestResult<Nothing>
}
