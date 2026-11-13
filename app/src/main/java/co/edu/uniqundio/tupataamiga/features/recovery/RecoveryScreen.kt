package co.edu.uniqundio.tupataamiga.features.recovery

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniqundio.tupataamiga.R
import co.edu.uniqundio.tupataamiga.domain.repository.UserRepository
import kotlinx.coroutines.launch

@Composable
fun RecoveryScreen(
    userRepository: UserRepository? = null,
    onBack: () -> Unit = {}
) {
    var correo by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var sentSuccess by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    fun validarCorreo(): Boolean {
        return if (!correo.contains("@")) {
            emailError = "Ingresa un correo electrónico válido"
            false
        } else {
            emailError = null
            true
        }
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Recuperación de Contraseña",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = mainTextColor,
                    fontSize = 22.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa tu correo registrado y te enviaremos un enlace oficial para restablecer tu contraseña.",
                style = MaterialTheme.typography.bodyMedium.copy(color = subTextColor),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (sentSuccess) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "✓ Enlace enviado con éxito",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32),
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Revisa tu bandeja de entrada en $correo para restablecer tu clave.",
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Correo electrónico registrado",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = subTextColor
                )
                TextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        if (emailError != null) emailError = null
                        if (sentSuccess) sentSuccess = false
                    },
                    placeholder = { Text("ejemplo@uniqundio.edu.co") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = MaterialTheme.colorScheme.error
                    ),
                    singleLine = true
                )
                emailError?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = {
                    if (validarCorreo()) {
                        coroutineScope.launch {
                            userRepository?.recuperarContrasena(correo)
                            sentSuccess = true
                            snackbarHostState.showSnackbar("Enlace de recuperación enviado a $correo")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = "ENVIAR ENLACE DE RECUPERACIÓN",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onBack) {
                Text(
                    text = "Volver al Inicio de Sesión",
                    color = brandGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
