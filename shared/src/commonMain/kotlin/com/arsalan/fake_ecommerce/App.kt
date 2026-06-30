package com.arsalan.fake_ecommerce

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arsalan.fake_ecommerce.ui.FauxCartApp

/**
 * Single shared entry point used by all platforms (Android Activity, iOS UIViewController, Web).
 * All UI, presentation and data logic lives in this shared module.
 */
@Composable
@Preview
fun App() {
    FauxCartApp()
}
