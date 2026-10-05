package co.edu.uniqundio.tupataamiga.features.recovery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RecoveryScreen(
    onBack: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var sent by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Recuperar Contraseña", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { sent = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar instrucciones")
        }
        if (sent) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Se han enviado instrucciones a tu correo", color = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("Volver")
        }
    }
}
