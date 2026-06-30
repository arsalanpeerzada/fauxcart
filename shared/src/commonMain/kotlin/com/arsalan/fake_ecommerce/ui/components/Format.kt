package com.arsalan.fake_ecommerce.ui.components

import kotlin.math.abs
import kotlin.math.round

/**
 * Multiplatform-safe USD formatter (no java.text dependency). Centralised so currency rendering is
 * identical everywhere (DRY). Example: 1250.0 -> "$1,250.00".
 */
fun formatUsd(amount: Double): String {
    val negative = amount < 0
    val cents = round(abs(amount) * 100).toLong()
    val whole = cents / 100
    val frac = (cents % 100).toString().padStart(2, '0')
    val grouped = whole.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return (if (negative) "-$" else "$") + grouped + "." + frac
}
