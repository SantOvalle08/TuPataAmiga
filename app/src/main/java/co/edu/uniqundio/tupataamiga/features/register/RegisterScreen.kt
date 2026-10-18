package co.edu.uniqundio.tupataamiga.features.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniqundio.tupataamiga.R
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = Color(0xFFF6FBF7)
    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)

    fun mostrarSnackbar(mensaje: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = mensaje)
        }
    }

    fun validarRegistro(): Boolean {
        var esValido = true
        if (uiState.nombre.isBlank()) {
            nameError = "Ingresa tu nombre completo"
            esValido = false
        } else {
            nameError = null
        }

        if (!uiState.correo.contains("@")) {
            emailError = "El correo debe incluir un '@'"
            esValido = false
        } else {
            emailError = null
        }

        if (uiState.clave.length < 8) {
            passwordError = "La contraseña debe tener al menos 8 caracteres"
            esValido = false
        } else {
            passwordError = null
        }

        if (!esValido) {
            mostrarSnackbar("Por favor, revisa los campos señalados")
        }
        return esValido
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(brandGreen),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_pataamiga),
                    contentDescription = "Logo PataAmiga",
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Crear Cuenta",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Únete a la comunidad de PataAmiga",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Form Inputs
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Name Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Nombre completo", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                    TextField(
                        value = uiState.nombre,
                        onValueChange = {
                            viewModel.onNombreChanged(it)
                            if (nameError != null) nameError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = nameError != null,
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            disabledContainerColor = fieldBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error
                        ),
                        singleLine = true
                    )
                    nameError?.let { Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                }

                // Email Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Correo electrónico", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                    TextField(
                        value = uiState.correo,
                        onValueChange = {
                            viewModel.onCorreoChanged(it)
                            if (emailError != null) emailError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = emailError != null,
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            disabledContainerColor = fieldBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error
                        ),
                        singleLine = true
                    )
                    emailError?.let { Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                }

                // Password Input
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Contraseña", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.DarkGray)
                    TextField(
                        value = uiState.clave,
                        onValueChange = {
                            viewModel.onClaveChanged(it)
                            if (passwordError != null) passwordError = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        isError = passwordError != null,
                        enabled = !uiState.isLoading,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            disabledContainerColor = fieldBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error
                        ),
                        singleLine = true
                    )
                    passwordError?.let { Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Register Button
            Button(
                onClick = {
                    if (validarRegistro()) {
                        viewModel.register(
                            onSuccess = onRegisterSuccess,
                            onError = { error -> mostrarSnackbar(error) }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                shape = RoundedCornerShape(25.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "REGISTRARME",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "¿Ya tienes cuenta? ", color = Color.Gray, fontSize = 14.sp)
                TextButton(
                    onClick = onBack,
                    enabled = !uiState.isLoading,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Inicia sesión",
                        color = brandGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}