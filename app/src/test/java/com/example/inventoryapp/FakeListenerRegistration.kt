package com.example.inventoryapp


import com.google.firebase.firestore.ListenerRegistration

class FakeListenerRegistration : ListenerRegistration {
    override fun remove() { /* nothing */ }
}