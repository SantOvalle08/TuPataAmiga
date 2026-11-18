package co.edu.uniqundio.tupataamiga.data.di

import co.edu.uniqundio.tupataamiga.data.repository.firebase.FirebaseMascotaRepositoryImpl
import co.edu.uniqundio.tupataamiga.data.repository.firebase.FirebaseNotificacionRepositoryImpl
import co.edu.uniqundio.tupataamiga.data.repository.firebase.FirebaseUserRepositoryImpl
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
import co.edu.uniqundio.tupataamiga.domain.repository.NotificacionRepository
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMascotaRepository(
        impl: FirebaseMascotaRepositoryImpl
    ): MascotaRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: FirebaseUserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindNotificacionRepository(
        impl: FirebaseNotificacionRepositoryImpl
    ): NotificacionRepository
}
