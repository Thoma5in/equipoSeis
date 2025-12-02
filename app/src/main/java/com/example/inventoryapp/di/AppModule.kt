package com.example.inventoryapp.di

import com.example.inventoryapp.repository.FirestoreInventoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirestoreInventoryRepository(): FirestoreInventoryRepository {
        return FirestoreInventoryRepository()
    }
}