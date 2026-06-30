package com.arsalan.fake_ecommerce.data.repository

import com.arsalan.fake_ecommerce.data.local.SavingsStore
import com.arsalan.fake_ecommerce.domain.repository.SavingsRepository
import com.arsalan.fake_ecommerce.domain.repository.SavingsState
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Maps the persisted key-value store onto the domain [SavingsRepository]. Recording an avoided
 * purchase is atomic under the mutex: total and streak are read, incremented and written as one
 * unit, so concurrent calls cannot interleave into an inconsistent total.
 */
class SavingsRepositoryImpl(
    private val store: SavingsStore,
) : SavingsRepository {

    private val lock = Mutex()

    override suspend fun load(): SavingsState = lock.withLock { snapshot() }

    override suspend fun recordAvoidedPurchase(amountSaved: Double): SavingsState = lock.withLock {
        val next = SavingsState(
            totalSaved = store.readTotalSaved() + amountSaved.coerceAtLeast(0.0),
            streak = store.readStreak() + 1,
        )
        store.write(next.totalSaved, next.streak)
        next
    }

    private fun snapshot() = SavingsState(
        totalSaved = store.readTotalSaved(),
        streak = store.readStreak(),
    )
}
