package com.arsalan.fake_ecommerce.data.local

import platform.Foundation.NSUserDefaults

/** Durable store backed by NSUserDefaults - persists across app launches on iOS. */
actual fun provideSavingsStore(): SavingsStore = object : SavingsStore {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun readTotalSaved(): Double = defaults.doubleForKey(SavingsKeys.TOTAL_SAVED)
    override fun readStreak(): Int = defaults.integerForKey(SavingsKeys.STREAK).toInt()

    override fun write(totalSaved: Double, streak: Int) {
        defaults.setDouble(totalSaved, SavingsKeys.TOTAL_SAVED)
        defaults.setInteger(streak.toLong(), SavingsKeys.STREAK)
    }
}
