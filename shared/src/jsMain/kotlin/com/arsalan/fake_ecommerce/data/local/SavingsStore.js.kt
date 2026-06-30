package com.arsalan.fake_ecommerce.data.local

/**
 * Web (JS) uses the in-memory store for now. A durable version can wrap `localStorage`; the exact
 * browser API surface depends on the kotlin-wrappers version in use, so it is left as a follow-up
 * to avoid pinning an API that may differ across versions.
 */
actual fun provideSavingsStore(): SavingsStore = InMemorySavingsStore()
