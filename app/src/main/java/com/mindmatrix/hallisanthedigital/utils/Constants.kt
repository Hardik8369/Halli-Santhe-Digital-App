package com.mindmatrix.hallisanthedigital.utils

import com.mindmatrix.hallisanthedigital.BuildConfig

object Constants {
    const val COLLECTION_PRODUCTS = "products"

    // ✅ Read from BuildConfig
    val GEMINI_API_KEY: String = BuildConfig.GEMINI_API_KEY

    // ✅ Switched back to v1beta which is more compatible for AI Studio keys
    const val GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"

    val CATEGORIES = listOf("All", "Food", "Craft", "Textile", "Other")
}