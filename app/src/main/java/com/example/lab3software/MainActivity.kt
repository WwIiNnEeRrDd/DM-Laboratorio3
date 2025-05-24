package com.example.lab3software

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.lab3software.ui.theme.Lab3SoftwareTheme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab3SoftwareTheme {
                GameScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen() {
    // Estados
    var target by remember { mutableStateOf(Random.nextInt(0, 101)) }
    var guessInput by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("¡Intenta adivinar el número!") }
    var attemptsLeft by remember { mutableStateOf(3) }
    var timeLeft by remember { mutableStateOf(60) }
    var gameOver by remember { mutableStateOf(false) }
    // indicador dinámico para parar el timer
    val timerFinished by remember {
        derivedStateOf { gameOver || attemptsLeft == 0 }
    }

    // Temporizador que se cancela automáticamente cuando timerFinished == true
    LaunchedEffect(timerFinished) {
        if (!timerFinished) {
            while (timeLeft > 0) {
                delay(1_000L)
                timeLeft--
            }
            // si llega a 0 antes de terminar el juego
            if (timeLeft == 0) {
                feedback = "⏰ ¡Se acabó el tiempo! El número era $target."
                gameOver = true
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(0.9f)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Adivina un número entre 0 y 100",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Intentos: $attemptsLeft", style = MaterialTheme.typography.bodyMedium)
                        Text("Tiempo: ${timeLeft}s", style = MaterialTheme.typography.bodyMedium)
                    }
                    OutlinedTextField(
                        value = guessInput,
                        onValueChange = { input ->
                            if (!gameOver && attemptsLeft > 0) {
                                guessInput = input.filter { it.isDigit() }
                            }
                        },
                        label = { Text("Tu número") },
                        singleLine = true,
                        enabled = !gameOver && attemptsLeft > 0,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Button(
                        onClick = {
                            val guess = guessInput.toIntOrNull()
                            if (guess == null) {
                                feedback = "❗ Ingresa un número válido."
                            } else {
                                attemptsLeft--
                                feedback = when {
                                    guess < target -> "↑ El número es mayor."
                                    guess > target -> "↓ El número es menor."
                                    else -> {
                                        gameOver = true
                                        "🎉 ¡Correcto! Era $target."
                                    }
                                }
                                if (attemptsLeft == 0 && !gameOver) {
                                    feedback = "❌ Se acabaron los intentos. Era $target."
                                    gameOver = true
                                }
                            }
                            guessInput = ""
                        },
                        enabled = !gameOver && attemptsLeft > 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Comprobar", color = MaterialTheme.colorScheme.onPrimary)
                    }

                    // Color dinámico del feedback
                    val feedbackColor = when {
                        gameOver && feedback.contains("Correcto") -> MaterialTheme.colorScheme.primary
                        gameOver -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        feedback,
                        style = MaterialTheme.typography.bodyLarge,
                        color = feedbackColor,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (gameOver) {
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                // reiniciar todo
                                target = Random.nextInt(0, 101)
                                attemptsLeft = 3
                                timeLeft = 60
                                feedback = "¡Intenta adivinar el número!"
                                guessInput = ""
                                gameOver = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Volver a jugar")
                        }
                    }
                }
            }
        }
    }
}
