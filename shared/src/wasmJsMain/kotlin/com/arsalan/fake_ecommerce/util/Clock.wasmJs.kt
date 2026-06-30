package com.arsalan.fake_ecommerce.util

@JsFun("() => Date.now()")
private external fun jsDateNow(): Double

actual fun nowMillis(): Long = jsDateNow().toLong()
