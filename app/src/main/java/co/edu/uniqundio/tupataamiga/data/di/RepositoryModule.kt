package co.edu.uniqundio.tupataamiga.data.di

import co.edu.uniqundio.tupataamiga.data.repository.memory.MascotaRepositoryImpl
import co.edu.uniqundio.tupataamiga.data.repository.memory.UserRepositoryImpl
import co.edu.uniqundio.tupataamiga.domain.repository.MascotaRepository
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
        impl: MascotaRepositoryImpl
    ): MascotaRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
