package com.example.carrinhocompras.design

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.carrinhocompras.model.Product
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProductInsertScreen(navController: NavController, db: FirebaseFirestore) {
    // Definir o estado para o nome, preço e URI da imagem
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") } // Usamos String para facilitar a entrada no TextField
    var imageUri by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf(false) } // Estado para controlar a exibição da mensagem de sucesso

    // Função para salvar o produto na Firebase
    fun saveProduct() {
        if (name.isNotBlank() && price.isNotBlank()) {
            // Convertemos o preço de String para Double
            val priceDouble = price.toDoubleOrNull()
            if (priceDouble != null) {
                val product = Product(name = name, price = priceDouble, imageUri = imageUri)

                // Salvar no Firestore
                val productCollection = db.collection("products") // Coleção de produtos
                val productId = productCollection.document() // Gera um ID único
                productId.set(product).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        successMessage = true // Exibir mensagem de sucesso
                        Toast.makeText(navController.context, "Produto inserido com sucesso!", Toast.LENGTH_SHORT).show()

                        // Limpar os campos
                        name = ""
                        price = ""
                        imageUri = ""

                        // Se o usuário desejar continuar inserindo produtos, a interface estará limpa
                    } else {
                        Toast.makeText(navController.context, "Erro ao salvar o produto: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(navController.context, "Preço inválido.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(navController.context, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
        }
    }

    // Controlar a duração da exibição da mensagem de sucesso
    LaunchedEffect(successMessage) {
        if (successMessage) {
            kotlinx.coroutines.delay(4000) // Espera por 4 segundos
            successMessage = false // Esconde a mensagem após 4 segundos
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Nome do Produto
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        // Preço do Produto
        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Preço") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        // URI da Imagem (simulação do caminho da imagem)
        OutlinedTextField(
            value = imageUri,
            onValueChange = { imageUri = it },
            label = { Text("Imagem URI") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Botão de salvar
        Button(
            onClick = { saveProduct() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar Produto")
        }

        // Mostrar mensagem de sucesso se o produto for inserido
        if (successMessage) {
            Text("Produto inserido com sucesso!", color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 16.dp))
        }

        // Botão para retornar ao login
        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Voltar ao Login")
        }
    }
}
