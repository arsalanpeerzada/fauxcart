package com.arsalan.fake_ecommerce.domain.model

/**
 * A completed (simulated) order kept for the Profile history and daily-transaction views.
 * [timestampMillis] is wall-clock epoch millis; [dayIndex] is the UTC day bucket used to group
 * transactions by day without needing a full date library.
 */
data class OrderRecord(
    val id: Int,
    val itemCount: Int,
    val amountSaved: Double,
    val timestampMillis: Long,
) {
    val dayIndex: Long get() = timestampMillis / 86_400_000L
}
