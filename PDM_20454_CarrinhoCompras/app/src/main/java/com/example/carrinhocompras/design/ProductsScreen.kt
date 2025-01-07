package com.example.carrinhocompras.design

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.carrinhocompras.repository.CartRepository
import com.example.carrinhocompras.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

@Composable
fun ProductsScreen(navController: NavController, db: FirebaseFirestore, userEmail: String) {
    val productsList = remember { mutableStateListOf<Product>() }
    val isLoading = remember { mutableStateOf(true) }
    val cartRepository = remember { CartRepository(db) }

    val auth = FirebaseAuth.getInstance()

    // Função para buscar os produtos da Firestore
    LaunchedEffect(Unit) {
        db.collection("products") // Nome da coleção no Firestore
            .get()
            .addOnSuccessListener { result ->
                isLoading.value = false
                productsList.clear()  // Limpa a lista antes de adicionar os novos produtos
                for (document in result) {
                    val product = document.toObject<Product>()
                    productsList.add(product)
                }
            }
            .addOnFailureListener { exception ->
                isLoading.value = false
                Toast.makeText(
                    navController.context,
                    "Erro ao carregar produtos: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    // Exibição de loading ou lista de produtos
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Botão para fazer logout
        Button(
            onClick = {
                auth.signOut() // Realiza o logout
                navController.navigate("login") // Redireciona para a tela de login
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp) // Espaço entre o botão e o conteúdo
        ) {
            Text(text = "Logout")
        }

        // Botão para ir para o carrinho no topo
        Button(
            onClick = {
                // Navegar para a tela do carrinho, passando o email do usuário
                navController.navigate("cart/$userEmail")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Ir para o Carrinho")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Exibição de loading ou lista de produtos
        if (isLoading.value) {
            LoadingScreen()
        } else {
            ProductsList(productsList, onAddToCart = { product ->
                cartRepository.addToCart(product, userEmail) // Passando o userEmail para o com.example.carrinhocompras.Repository.CartRepository
            })
        }
    }
}

@Composable
fun ProductsList(productsList: List<Product>, onAddToCart: (Product) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp) // Ajuste do espaçamento lateral para um visual mais espaçoso
            .background(Color(0xFFF7F7F7)) // Fundo suave e agradável (cinza claro com um toque moderno)
    ) {
        items(productsList) { product ->
            ProductCard(product = product, onAddToCart = onAddToCart)
        }
    }
}

@Composable
fun ProductCard(product: Product, onAddToCart: (Product) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp) // Menor espaço entre os cards
            .background(Color.White, shape = RoundedCornerShape(20.dp)) // Borda arredondada
            .shadow(12.dp, RoundedCornerShape(20.dp)) // Sombra mais suave e moderna
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp) // Padding com mais espaço para um visual clean
        ) {
            // Exibindo a imagem do produto
            product.imageUri?.let {
                AsyncImage(
                    model = it,
                    contentDescription = "Imagem do produto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp) // Mantém a altura da imagem
                        .clip(RoundedCornerShape(12.dp)), // Bordas arredondadas
                    contentScale = ContentScale.Fit // A imagem será ajustada para caber no espaço sem cortar
                )
            }
            Spacer(modifier = Modifier.height(12.dp)) // Menor espaço entre a imagem e o texto
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE) // Roxo moderno para o título
                )
            )
            Spacer(modifier = Modifier.height(6.dp)) // Menor espaço entre o nome e o preço
            Text(
                text = "R$ ${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFFF5722), // Laranja vibrante para o preço
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { onAddToCart(product) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Adicionar ao Carrinho")
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .background(Color(0xFFF1F1F1)) // Fundo mais suave para o loading
    ) {
        CircularProgressIndicator(
            color = Color(0xFF6200EE), // Cor personalizada roxa para o indicador
            strokeWidth = 4.dp
        )
    }
}
