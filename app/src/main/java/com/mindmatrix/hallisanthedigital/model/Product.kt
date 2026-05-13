package com.mindmatrix.hallisanthedigital.model

data class Product(
    val productId: String = "",
    val name: String = "",
    val price: Double = 0.0,        // ✅ Double (not String) - matches saveToFirestore()
    val category: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val sellerPhone: String = "",
    val sellerName: String = "",
    val sellerId: String = "",
    val timestamp: Long = 0L        // ✅ Long - matches System.currentTimeMillis()
)