// Product
package com.example.carrinhocompras.model

data class Product(
    var name: String = "",
    var price: Double = 0.0,  // Mudado para Double
    var imageUri: String? = null
)
