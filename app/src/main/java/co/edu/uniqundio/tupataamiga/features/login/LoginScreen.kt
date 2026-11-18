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
import co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateRecovery: () -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var emailError by remember { mutableStateOf<String?>(value = null) }
    var passwordError by remember { mutableStateOf<String?>(value = null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    fun mostrarSnackbar(mensaje: String) {
        scope.launch {
            snackbarHostState.showSnackbar(message = mensaje)
        }
    }

    fun validarFormulario(): Boolean {
        var esValido = true
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.correo.trim()).matches()) {
            emailError = LocaleManager.tr("Ingresa un correo válido", "Enter a valid email")
            esValido = false
        } else {
            emailError = null
        }
        if (uiState.clave.length < 6) {
            passwordError = LocaleManager.tr("Ingresa una contraseña válida", "Enter a valid password")
            esValido = false
        } else {
            passwordError = null
        }
        if (!esValido) {
            mostrarSnackbar(LocaleManager.tr("Revisa el correo y la contraseña", "Please check your email and password"))
        }
        return esValido
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        containerColor = backgroundColor,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(brandGreen),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_pataamiga),
                    contentDescription = "Logo PataAmiga",
                    modifier = Modifier.size(50.dp),
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = LocaleManager.tr("Bienvenido de nuevo", "Welcome back"),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = mainTextColor,
                    fontSize = 20.sp,
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = LocaleManager.tr("Inicia sesión en PataAmiga", "Sign in to PataAmiga"),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = subTextColor,
                    fontSize = 12.sp,
                ),
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = LocaleManager.tr("Correo electrónico", "Email address"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextColor,
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
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                        ),
                        singleLine = true,
                    )
                    emailError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                        )
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = LocaleManager.tr("Contraseña", "Password"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextColor,
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
                            errorIndicatorColor = MaterialTheme.colorScheme.error,
                        ),
                        singleLine = true,
                    )
                    passwordError?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 11.sp,
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                TextButton(
                    onClick = onNavigateRecovery,
                    enabled = !uiState.isLoading,
                ) {
                    Text(
                        text = LocaleManager.tr("¿Olvidaste tu contraseña?", "Forgot your password?"),
                        color = brandGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    if (validarFormulario()) {
                        viewModel.login(
                            onSuccess = {
                                mostrarSnackbar(LocaleManager.tr("¡Inicio de sesión exitoso!", "Sign in successful!"))
                                scope.launch {
                                    delay(600.milliseconds)
                                    onLoginSuccess()
                                }
                            },
                            onError = { error ->
                                mostrarSnackbar(error)
                            },
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = brandGreen,
                ),
                shape = RoundedCornerShape(23.dp),
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                    )
                } else {
                    Text(
                        text = LocaleManager.tr("INICIAR SESIÓN", "LOG IN"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = LocaleManager.tr("¿No tienes cuenta? ", "Don't have an account? "),
                    color = subTextColor,
                    fontSize = 12.sp,
                )
                TextButton(
                    onClick = onBack,
                    enabled = !uiState.isLoading,
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Text(
                        text = LocaleManager.tr("Regístrate", "Sign Up"),
                        color = brandGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}
