package com.arsalan.fake_ecommerce.data.local

/**
 * Web (Wasm) uses the in-memory store for now, mirroring the JS target. A durable `localStorage`
 * implementation is a follow-up.
 */
actual fun provideSavingsStore(): SavingsStore = InMemorySavingsStore()
