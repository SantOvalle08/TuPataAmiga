package co.edu.uniqundio.tupataamiga.features.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Estados para controlar los errores de validación
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Función que valida las reglas antes de registrar
    fun validarRegistro(): Boolean {
        var esValido = true

        // Regla 1: Que el correo contenga un '@'
        if (!uiState.correo.contains("@")) {
            emailError = "El correo debe incluir un '@'"
            esValido = false
        } else {
            emailError = null
        }

        // Regla 2: Que la contraseña tenga al menos 8 caracteres
        if (uiState.clave.length < 8) {
            passwordError = "La contraseña debe tener al menos 8 caracteres"
            esValido = false
        } else {
            passwordError = null
        }

        return esValido
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Registro", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = viewModel::onNombreChanged,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Correo con validación y mensaje de error
        OutlinedTextField(
            value = uiState.correo,
            onValueChange = {
                viewModel.onCorreoChanged(it)
                if (emailError != null) emailError = null
            },
            label = { Text("Correo") },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(text = emailError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña con validación y mensaje de error
        OutlinedTextField(
            value = uiState.clave,
            onValueChange = {
                viewModel.onClaveChanged(it)
                if (passwordError != null) passwordError = null
            },
            label = { Text("Contraseña") },
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(text = passwordError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Solo llama al backend si pasa las validaciones de correo y contraseña
                if (validarRegistro()) {
                    viewModel.register(
                        onSuccess = onRegisterSuccess,
                        onError = { errorMessage = it }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Registrarse")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("Volver")
        }
    }
}