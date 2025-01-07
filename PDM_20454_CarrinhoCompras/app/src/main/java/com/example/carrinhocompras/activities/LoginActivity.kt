package com.example.carrinhocompras.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.carrinhocompras.design.LoginScreen
import com.example.carrinhocompras.ui.theme.CarrinhoComprasTheme
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            CarrinhoComprasTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController: NavHostController = rememberNavController()
                    LoginScreen(navController) { email, password ->
                        loginUser(email, password, navController)
                    }
                }
            }
        }
    }

    private fun loginUser(email: String, password: String, navController: NavHostController) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login bem-sucedido
                        val user = auth.currentUser
                        if (user?.email == "diogo@123.com") {
                            // Se o email for "diogo@123.com", navegar para a tela de inserção de produto
                            navController.navigate("product_insert")
                        } else {
                            // Caso contrário, navegar para a tela de produtos normais
                            navController.navigate("products")
                        }
                        Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // Falha no login
                        Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
        }
    }
}
