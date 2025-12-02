package com.example.inventoryapp.di

import com.example.inventoryapp.repository.FirestoreInventoryRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun firestoreInventoryRepository(): FirestoreInventoryRepository
    fun firebaseAuth(): FirebaseAuth
}