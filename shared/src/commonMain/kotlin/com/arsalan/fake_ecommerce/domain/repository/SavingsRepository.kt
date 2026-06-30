package com.arsalan.fake_ecommerce.domain.repository

/** The only data that must outlive a session: cumulative savings and the streak. */
data class SavingsState(
    val totalSaved: Double = 0.0,
    val streak: Int = 0,
)

/**
 * Persists the savings/streak (the "Durability" of the ACID-in-spirit model). A separate, focused
 * interface (Interface Segregation) so the catalogue and persistence concerns never bleed together.
 */
interface SavingsRepository {
    /** Reads the persisted savings/streak, or defaults on first run. */
    suspend fun load(): SavingsState

    /** Records one avoided purchase: adds [amountSaved] to the total and increments the streak. */
    suspend fun recordAvoidedPurchase(amountSaved: Double): SavingsState
}
