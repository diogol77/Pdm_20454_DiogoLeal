package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configura o conteúdo da atividade como a calculadora
        setContent {
            MyApplicationTheme {
                CalculatorApp()
            }
        }
    }
}

@Composable
fun CalculatorApp() {
    // Variáveis de estado para a interface da calculadora
    var displayText by remember { mutableStateOf("0") }
    var firstNumber by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var secondNumber by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Visor da calculadora
        Text(
            text = displayText,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFFBBDEFB)) // Azul claro
                .padding(16.dp),
            maxLines = 1
        )

        // Grade de botões de números
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val buttons = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9")
            )
            // Loop para criar os botões de número
            buttons.forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    row.forEach { label ->
                        CalculatorButton(label) {
                            // Se o operador não foi pressionado, usa-se o firstNumber
                            if (operator.isEmpty()) {
                                // Remove o "0" inicial antes de adicionar outro número
                                if (firstNumber == "0") {
                                    firstNumber = label
                                } else {
                                    firstNumber += label
                                }
                                displayText = firstNumber
                            } else {
                                // Mesma lógica para o segundo número
                                if (secondNumber == "0") {
                                    secondNumber = label
                                } else {
                                    secondNumber += label
                                }
                                displayText = secondNumber
                            }
                        }
                    }
                }
            }

            // Botões de operações
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                CalculatorButton("+") { operator = "+" }
                CalculatorButton("-") { operator = "-" }
                CalculatorButton("*") { operator = "*" }
                CalculatorButton("/") { operator = "/" }
            }

            // Botões de igual, limpar e apagar
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botão de limpar (C)
                CalculatorButton("C") {
                    // Reseta tudo ao valor inicial
                    displayText = "0"
                    firstNumber = ""
                    secondNumber = ""
                    operator = ""
                }

                // Botão de apagar (←)
                CalculatorButton("←") {
                    if (operator.isEmpty()) {
                        // Remove o último caractere do primeiro número, mas não deixe como string vazia
                        firstNumber = firstNumber.dropLast(1).ifEmpty { "0" }
                        displayText = firstNumber
                    } else {
                        // Remove o último caractere do segundo número, mas não deixe como string vazia
                        secondNumber = secondNumber.dropLast(1).ifEmpty { "0" }
                        displayText = secondNumber
                    }
                }

                // Botão de resultado
                CalculatorButton("=") {
                    if (firstNumber.isNotEmpty() && secondNumber.isNotEmpty() && operator.isNotEmpty()) {
                        // Calcula o resultado
                        val result = calculateResult(
                            firstNumber.toDouble(),
                            secondNumber.toDouble(),
                            operator
                        )
                        // Formata o resultado com até 6 dígitos decimais usando Locale.US
                        displayText = String.format(Locale.US, "%.6f", result).trimEnd('0').trimEnd('.')
                        firstNumber = result.toString()
                        secondNumber = ""
                        operator = ""
                    }
                }
            }
        }
    }
}

// Função que desenha os botões da calculadora
@Composable
fun CalculatorButton(label: String, onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(64.dp)
            .background(Color(0xFF42A5F5)) // Azul médio
            .clickable { onClick() }
    ) {
        Text(text = label, fontSize = 24.sp, color = Color.White)
    }
}

// Função para realizar o cálculo
fun calculateResult(firstNumber: Double, secondNumber: Double, operator: String): Double {
    return when (operator) {
        "+" -> firstNumber + secondNumber
        "-" -> firstNumber - secondNumber
        "*" -> firstNumber * secondNumber
        "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else 0.0
        else -> 0.0
    }
}

// Preview da calculadora
@Preview(showBackground = true)
@Composable
fun CalculatorAppPreview() {
    MyApplicationTheme {
        CalculatorApp()
    }
}
