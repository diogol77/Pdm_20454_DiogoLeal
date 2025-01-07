package com.example.carrinhocompras.design

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.carrinhocompras.model.CartItem
import com.example.carrinhocompras.repository.CartRepository

@Composable
fun SharedCartScreen(
    userEmail: String,
    cartRepository: CartRepository,
    navController: NavController
) {
    var sharedCarts by remember { mutableStateOf<Map<String, List<CartItem>>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userEmail) {
        cartRepository.getSharedCarts(userEmail, onSuccess = { carts ->
            sharedCarts = carts
            isLoading = false
        }, onFailure = { exception ->
            isLoading = false
        })
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar")
            }

            Text(
                text = "Carrinhos Compartilhados Consigo",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (sharedCarts.isEmpty()) {
                Text(text = "Não há carrinhos compartilhados.")
            } else {
                LazyColumn {
                    sharedCarts.forEach { (sharedBy, items) ->
                        item {
                            Text(
                                text = "Carrinho de $sharedBy",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        items(items) { cartItem ->
                            SharedCartItemRow(
                                cartItem = cartItem,
                                sharedByUser = sharedBy,
                                userEmail = userEmail,
                                cartRepository = cartRepository,
                                onQuantityUpdated = { updatedItem ->
                                    // Atualizar o item com a nova quantidade
                                    sharedCarts = sharedCarts.mapValues { (key, value) ->
                                        if (key == sharedBy) {
                                            value.map { item ->
                                                if (item.productId == updatedItem.productId) updatedItem else item
                                            }
                                        } else value
                                    }
                                },
                                onRemoveSharedCart = {
                                    // Remover o carrinho compartilhado
                                    cartRepository.removeSharedCart(userEmail, sharedBy)
                                    sharedCarts = sharedCarts - sharedBy
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SharedCartItemRow(
    cartItem: CartItem,
    sharedByUser: String,
    userEmail: String,
    cartRepository: CartRepository,
    onQuantityUpdated: (CartItem) -> Unit,
    onRemoveSharedCart: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = cartItem.name)
            Text(text = "Quantidade: ${cartItem.quantity}")
            // Exibição do preço total, calculado com a quantidade
            Text(text = "Preço Total: €${"%.2f".format(cartItem.quantity * cartItem.price)}")
        }

        Row {
            // Botão para aumentar a quantidade
            IconButton(onClick = {
                cartRepository.updateSharedCartItemQuantity(
                    sharedByUser = sharedByUser,
                    userEmail = userEmail,
                    item = cartItem,
                    increment = 1,
                    onUpdated = { updatedItem -> onQuantityUpdated(updatedItem) },
                    onFailure = { /* Lidar com erro */ }
                )
            }) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Aumentar Quantidade")
            }

            // Botão para diminuir a quantidade
            IconButton(onClick = {
                cartRepository.updateSharedCartItemQuantity(
                    sharedByUser = sharedByUser,
                    userEmail = userEmail,
                    item = cartItem,
                    increment = -1,
                    onUpdated = { updatedItem -> onQuantityUpdated(updatedItem) },
                    onFailure = { /* Lidar com erro */ }
                )
            }) {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Diminuir Quantidade")
            }
        }

        // Botão para remover o item compartilhado
        IconButton(onClick = onRemoveSharedCart) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remover Carrinho")
        }
    }
}
