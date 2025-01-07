package com.example.carrinhocompras.design

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.carrinhocompras.model.CartItem
import com.example.carrinhocompras.repository.CartRepository

@Composable
fun CartScreen(
    cartRepository: CartRepository,
    userEmail: String,
    onItemRemoved: () -> Unit,
    onItemUpdated: () -> Unit,
    navController: NavController // Para navegação
) {
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedUserEmail by remember { mutableStateOf("") } // Email para compartilhar o carrinho
    var isCartShared by remember { mutableStateOf(false) } // Flag para indicar se o carrinho foi compartilhado
    var successMessageVisible by remember { mutableStateOf(false) } // Flag para mostrar a mensagem de sucesso

    // Carregar o carrinho do utilizador
    LaunchedEffect(userEmail) {
        try {
            cartRepository.getCart(userEmail, onSuccess = { items ->
                cartItems = items
                isLoading = false
            }, onFailure = { exception ->
                // Tratar erro (mostre uma mensagem de erro para o usuário ou registre no log)
                isLoading = false
                Log.e("CartScreen", "Erro ao carregar o carrinho: ${exception.message}")
            })
        } catch (e: Exception) {
            isLoading = false
            Log.e("CartScreen", "Erro inesperado: ${e.message}")
        }
    }

    // Funções para manipular os itens no carrinho
    fun handleQuantityChange(item: CartItem, increment: Int) {
        cartRepository.updateItemQuantity(item, userEmail, increment) { updatedItem ->
            cartItems = cartItems.map { if (it.name == updatedItem.name) updatedItem else it }
            onItemUpdated()
        }
    }

    fun handleRemoveItem(item: CartItem) {
        cartRepository.deleteItem(item, userEmail)
        cartItems = cartItems.filterNot { it.name == item.name }
        onItemRemoved()
    }

    // Função para limpar o carrinho inteiro
    fun handleClearCart() {
        cartRepository.clearCart(userEmail)
        cartItems = emptyList() // Limpa a lista de itens
        onItemRemoved() // Chama a função de callback para indicar que os itens foram removidos
    }

    // Função para compartilhar o carrinho
    fun handleShareCart() {
        if (selectedUserEmail.isNotEmpty()) {
            cartRepository.shareCartWithUser(userEmail, selectedUserEmail, cartItems)
            successMessageVisible = true
            isCartShared = true // O carrinho foi compartilhado, bloquear o campo de email
        }
    }

    // Calcular o valor total
    val totalPrice = cartItems.sumOf { it.price * it.quantity }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Adicionando o botão de voltar para produtos como item da lista
            item {
                IconButton(
                    onClick = { navController.navigate("products/$userEmail") },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Voltar para Produtos")
                }
            }

            // Adicionando o botão de limpar carrinho como item da lista
            item {
                Button(
                    onClick = { handleClearCart() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Limpar Carrinho")
                }
            }

            // Adicionando o campo de email como item da lista
            item {
                TextField(
                    value = selectedUserEmail,
                    onValueChange = { selectedUserEmail = it },
                    label = { Text("Insira o email com quem deseja partilhar o carrinho") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isCartShared // Bloquear o campo de email após o compartilhamento
                )
            }

            // Exibindo a mensagem de sucesso após o compartilhamento
            if (successMessageVisible) {
                item {
                    Text(
                        text = "Carrinho compartilhado com sucesso!",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Adicionando os itens do carrinho como item da lista
            items(cartItems) { item ->
                CartItemRow(
                    cartItem = item,
                    onIncreaseQuantity = { handleQuantityChange(item, 1) },
                    onDecreaseQuantity = { handleQuantityChange(item, -1) },
                    onRemoveItem = { handleRemoveItem(item) }
                )
            }

            // Adicionando o valor total como item da lista
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Espaçamento entre os itens e o total
                Text(
                    text = "Valor Total: €${"%.2f".format(totalPrice)}",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Adicionando o botão de compartilhar carrinho como item da lista
            item {
                Button(
                    onClick = { handleShareCart() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    enabled = !isCartShared // Desabilitar o botão de compartilhar após o compartilhamento
                ) {
                    Text(text = "Compartilhar Carrinho")
                }
            }

            // Adicionando o botão de visualizar carrinhos compartilhados como item da lista
            item {
                Button(
                    onClick = { navController.navigate("shared_cart_screen/$userEmail") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(text = "Carrinhos Compartilhados consigo")
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    cartItem: CartItem,
    onIncreaseQuantity: () -> Unit,
    onDecreaseQuantity: () -> Unit,
    onRemoveItem: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = cartItem.name)
            Text(text = "Quantidade: ${cartItem.quantity}")
            Text(text = "Preço: €${cartItem.price}")
        }

        // Botões de controle de quantidade (Adicionar primeiro)
        Row {
            IconButton(onClick = onIncreaseQuantity) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Aumentar Quantidade")
            }

            IconButton(onClick = onDecreaseQuantity) {
                Icon(imageVector = Icons.Filled.KeyboardArrowDown, contentDescription = "Diminuir Quantidade")
            }
        }

        // Botão para remover o item
        IconButton(onClick = onRemoveItem) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Remover Item")
        }
    }
}
