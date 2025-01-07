package com.example.carrinhocompras

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carrinhocompras.design.CartScreen
import com.example.carrinhocompras.design.LoginScreen
import com.example.carrinhocompras.design.ProductInsertScreen
import com.example.carrinhocompras.design.ProductsScreen
import com.example.carrinhocompras.design.RegisterScreen
import com.example.carrinhocompras.design.SharedCartScreen
import com.example.carrinhocompras.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carrinhocompras.ui.theme.CarrinhoComprasTheme

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarrinhoComprasTheme {
                MainApp(auth, db)
            }
        }
    }
}

@Composable
fun MainApp(auth: FirebaseAuth, db: FirebaseFirestore) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController, onLogin = { email, password ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user?.email == "diogo@123.com") {
                                // Se for o email desejado, navegar para a tela de inserção de produto
                                navController.navigate("product_insert") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                // Navegar para a tela de produtos passando o email do usuário
                                val userEmail = user?.email ?: ""
                                navController.navigate("products/$userEmail") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        } else {
                            // Mostrar mensagem de erro com mais detalhes
                            val errorMessage = task.exception?.message ?: "Erro desconhecido"
                            Toast.makeText(
                                navController.context,
                                "Erro ao fazer login: $errorMessage",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            })
        }

        composable("register") {
            RegisterScreen(onRegister = { email, password ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate("login")
                        } else {
                            Toast.makeText(
                                navController.context,
                                "Erro ao registrar: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            })
        }

        composable("product_insert") {
            ProductInsertScreen(navController = navController, db = db)
        }

        composable("products/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            ProductsScreen(navController = navController, db = db, userEmail = userEmail)
        }

        composable("cart/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            CartScreen(
                navController = navController, // Passando navController para a CartScreen
                cartRepository = CartRepository(db), // Passando o db para o repositório
                userEmail = userEmail,
                onItemRemoved = {
                    // Lógica de callback para item removido
                },
                onItemUpdated = {
                    // Lógica de callback para item atualizado
                }
            )
        }

        // Nova tela para o carrinho compartilhado
        composable("shared_cart_screen/{userEmail}") { backStackEntry ->
            val userEmail = backStackEntry.arguments?.getString("userEmail") ?: ""
            SharedCartScreen(
                userEmail = userEmail, // Passando o email do usuário
                cartRepository = CartRepository(db), // Passando o repositório
                navController = navController
            )
        }
    }
}
