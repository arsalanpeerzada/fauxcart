package com.arsalan.fake_ecommerce.data.local

/**
 * Tiny key-value abstraction for the only durable data in the app (total saved + streak).
 * Synchronous and platform-agnostic; concrete platform stores are provided via [provideSavingsStore].
 */
interface SavingsStore {
    fun readTotalSaved(): Double
    fun readStreak(): Int
    fun write(totalSaved: Double, streak: Int)
}

/**
 * In-memory fallback. Survives screen rotation (the owning container outlives the Activity via the
 * ViewModel), but not full process death. Used as a default and in tests.
 */
class InMemorySavingsStore(
    private var totalSaved: Double = 0.0,
    private var streak: Int = 0,
) : SavingsStore {
    override fun readTotalSaved(): Double = totalSaved
    override fun readStreak(): Int = streak
    override fun write(totalSaved: Double, streak: Int) {
        this.totalSaved = totalSaved
        this.streak = streak
    }
}

/** Provides the best durable store available for the current platform. */
expect fun provideSavingsStore(): SavingsStore

internal object SavingsKeys {
    const val TOTAL_SAVED = "fauxcart.total_saved"
    const val STREAK = "fauxcart.streak"
}
