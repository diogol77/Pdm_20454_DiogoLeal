package com.example.carrinhocompras.model

// com.example.carrinhocompras.Model.CartItem
data class CartItem(
    val productId: String = "",       // ID do produto
    val userEmail: String = "",       // Email do usuário associado
    val name: String = "",            // Nome do produto
    val price: Double = 0.0,          // Preço unitário
    val quantity: Int = 1,            // Quantidade
    val totalPrice: Double = 0.0      // Preço total (quantidade * preço)
)
