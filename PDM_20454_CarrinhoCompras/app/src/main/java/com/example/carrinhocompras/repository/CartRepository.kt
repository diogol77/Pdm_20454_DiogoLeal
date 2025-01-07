package com.example.carrinhocompras.repository

import com.example.carrinhocompras.model.CartItem
import com.example.carrinhocompras.model.Product
import com.google.firebase.firestore.FirebaseFirestore

class CartRepository(private val db: FirebaseFirestore) {

    // Função para adicionar ou atualizar um produto no carrinho
    fun addToCart(product: Product, userEmail: String) {
        val cartRef = db.collection("users").document(userEmail).collection("cart")

        cartRef.document(product.name).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentQuantity = (document.getLong("quantity") ?: 0).toInt()
                val newQuantity = currentQuantity + 1
                val totalPrice = product.price * newQuantity
                cartRef.document(product.name).update("quantity", newQuantity, "totalPrice", totalPrice)
            } else {
                val totalPrice = product.price
                val cartItem = CartItem(
                    productId = product.name,
                    userEmail = userEmail,
                    name = product.name,
                    price = product.price,
                    quantity = 1,
                    totalPrice = totalPrice
                )
                cartRef.document(product.name).set(cartItem)
            }
        }
    }

    // Função para atualizar a quantidade de um item no carrinho
    fun updateItemQuantity(item: CartItem, userEmail: String, increment: Int, onUpdated: (CartItem) -> Unit) {
        val cartRef = db.collection("users").document(userEmail).collection("cart")

        cartRef.document(item.name).get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentQuantity = (document.getLong("quantity") ?: 0).toInt()
                val newQuantity = currentQuantity + increment
                if (newQuantity > 0) {
                    val totalPrice = item.price * newQuantity
                    cartRef.document(item.name).update("quantity", newQuantity, "totalPrice", totalPrice).addOnSuccessListener {
                        val updatedItem = item.copy(quantity = newQuantity, totalPrice = totalPrice)
                        onUpdated(updatedItem)
                    }
                } else {
                    deleteItem(item, userEmail)
                }
            }
        }
    }

    // Função para excluir um item do carrinho
    fun deleteItem(item: CartItem, userEmail: String) {
        val cartRef = db.collection("users").document(userEmail).collection("cart")
        cartRef.document(item.name).delete()
    }

    // Função para obter o carrinho do usuário
    fun getCart(userEmail: String, onSuccess: (List<CartItem>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users")
            .document(userEmail)
            .collection("cart")
            .get()
            .addOnSuccessListener { result ->
                val cartItems = mutableListOf<CartItem>()
                for (document in result) {
                    val cartItem = document.toObject(CartItem::class.java)
                    cartItems.add(cartItem)
                }
                onSuccess(cartItems)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Função para limpar o carrinho
    fun clearCart(userEmail: String) {
        val cartRef = db.collection("users").document(userEmail).collection("cart")
        cartRef.get().addOnSuccessListener { result ->
            for (document in result) {
                document.reference.delete()
            }
        }
    }

    // Partilhar carrinho com outro usuário
    fun shareCartWithUser(userEmail: String, selectedUserEmail: String, cartItems: List<CartItem>) {
        val sharedCartRef = db.collection("shared_carts")
            .document(selectedUserEmail)
            .collection("sharedBy")
            .document(userEmail)

        val sharedItems = cartItems.map { item ->
            mapOf(
                "productId" to item.productId,
                "name" to item.name,
                "price" to item.price,
                "quantity" to item.quantity,
                "totalPrice" to item.totalPrice
            )
        }

        sharedCartRef.set(mapOf("cartItems" to sharedItems))
    }

    // Obter carrinhos partilhados
    fun getSharedCarts(
        userEmail: String,
        onSuccess: (Map<String, List<CartItem>>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("shared_carts")
            .document(userEmail)
            .collection("sharedBy")
            .get()
            .addOnSuccessListener { documents ->
                val sharedCarts = mutableMapOf<String, List<CartItem>>()
                for (doc in documents) {
                    val sharedByUser = doc.id
                    val cartItems = (doc.get("cartItems") as? List<Map<String, Any>>)?.map {
                        CartItem(
                            productId = it["productId"] as String,
                            name = it["name"] as String,
                            price = (it["price"] as Number).toDouble(),
                            quantity = (it["quantity"] as Number).toInt(),
                            totalPrice = (it["totalPrice"] as Number).toDouble()
                        )
                    } ?: emptyList()
                    sharedCarts[sharedByUser] = cartItems
                }
                onSuccess(sharedCarts)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Remover carrinho partilhado
    fun removeSharedCart(userEmail: String, sharedByUser: String) {
        db.collection("shared_carts")
            .document(userEmail)
            .collection("sharedBy")
            .document(sharedByUser)
            .delete()



    }


    // Atualizar a quantidade de um item no carrinho compartilhado
    fun updateSharedCartItemQuantity(
        sharedByUser: String,
        userEmail: String,
        item: CartItem,
        increment: Int,
        onUpdated: (CartItem) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val sharedCartRef = db.collection("shared_carts")
            .document(userEmail)
            .collection("sharedBy")
            .document(sharedByUser)

        sharedCartRef.get().addOnSuccessListener { document ->
            val cartItems = document.get("cartItems") as? List<Map<String, Any>> ?: emptyList()
            val updatedItems = cartItems.map { cartItemMap ->
                if (cartItemMap["productId"] == item.productId) {
                    val currentQuantity = (cartItemMap["quantity"] as Number).toInt()
                    val newQuantity = currentQuantity + increment
                    if (newQuantity > 0) {
                        mapOf(
                            "productId" to item.productId,
                            "name" to item.name,
                            "price" to item.price,
                            "quantity" to newQuantity,
                            "totalPrice" to newQuantity * item.price
                        )
                    } else null // Remove o item se a quantidade for zero
                } else cartItemMap
            }.filterNotNull()

            sharedCartRef.update("cartItems", updatedItems).addOnSuccessListener {
                val updatedItem = item.copy(
                    quantity = item.quantity + increment,
                    totalPrice = item.price * (item.quantity + increment)
                )
                onUpdated(updatedItem)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

}
