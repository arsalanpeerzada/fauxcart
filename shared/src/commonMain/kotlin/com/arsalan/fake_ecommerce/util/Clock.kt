package com.arsalan.fake_ecommerce.util

/** Wall-clock epoch milliseconds. Implemented per platform to avoid an extra date dependency. */
expect fun nowMillis(): Long
