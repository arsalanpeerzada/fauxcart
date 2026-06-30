package com.arsalan.fake_ecommerce

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform