package com.arsalan.fake_ecommerce.util

actual fun nowMillis(): Long = kotlin.js.Date.now().toLong()
