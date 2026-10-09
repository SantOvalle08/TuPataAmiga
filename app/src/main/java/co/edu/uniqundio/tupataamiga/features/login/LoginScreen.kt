package co.edu.uniqundio.tupataamiga.features.login
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateRecovery: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backgroundColor = Color(0xFFF6FBF7)
    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)
    fun mostrarSnackbar(mensaje: String) {
        scope.launch {
            snackbarHostState.showSnackbar(mensaje)
        }
    }
    fun validarFormulario(): Boolean {
        var esValido = true
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
            mostrarSnackbar("Revisa el correo y la contraseña")
        }
        return esValido
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(brandGreen),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_pataamiga),
                    contentDescription = "Logo PataAmiga",
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 24.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Inicia sesión en PataAmiga",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray,
                    fontSize = 14.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Correo electrónico",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
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
                            errorContainerColor = fieldBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error
                        ),
                        singleLine = true
                    )
                    emailError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Contraseña",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
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
                            errorContainerColor = fieldBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = MaterialTheme.colorScheme.error
                        ),
                        singleLine = true
                    )
                    passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                TextButton(
                    onClick = onNavigateRecovery,
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        color = brandGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (validarFormulario()) {
                        viewModel.login(
                            onSuccess = onLoginSuccess,
                            onError = { error ->
                                val msg = error.lowercase()
                                when {
                                    msg.contains("correo") ||
                                            msg.contains("user") ||
                                            msg.contains("usuario") -> {
                                        emailError =
                                            "El correo electrónico es incorrecto o no existe"
                                        mostrarSnackbar(
                                            "El correo electrónico es incorrecto o no existe"
                                        )
                                    }
                                    msg.contains("clave") ||
                                            msg.contains("contraseña") ||
                                            msg.contains("password") -> {
                                        passwordError = "La contraseña es incorrecta"
                                        mostrarSnackbar("La contraseña es incorrecta")
                                    }
                                    else -> {
                                        emailError = "Correo o contraseña incorrectos"
                                        passwordError = "Correo o contraseña incorrectos"
                                        mostrarSnackbar("Correo o contraseña incorrectos")
                                    }
                                }
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandGreen
                ),
                shape = RoundedCornerShape(25.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = onBack,
                    enabled = !uiState.isLoading,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Regístrate",
                        color = brandGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}