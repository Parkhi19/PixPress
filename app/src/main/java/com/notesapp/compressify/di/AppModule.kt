package com.notesapp.compressify.di

import com.notesapp.compressify.data.repository.LibraryRepository
import com.notesapp.compressify.domain.repository.LibraryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.DefineComponent
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule  {

    @Provides
    @Singleton
    fun provideLibraryRepository() : LibraryRepository {
        return LibraryRepositoryImpl()
    }
}