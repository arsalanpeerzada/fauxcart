package com.arsalan.fake_ecommerce.data.local

/**
 * Android currently uses the in-memory store (survives rotation via the retained ViewModel/container).
 * Durable persistence on Android needs a Context-backed store (DataStore or SharedPreferences); wiring
 * that through requires passing the Application context into the DI container and is a follow-up.
 */
actual fun provideSavingsStore(): SavingsStore = InMemorySavingsStore()
