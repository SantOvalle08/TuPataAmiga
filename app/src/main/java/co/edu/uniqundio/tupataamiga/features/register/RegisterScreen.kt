package co.edu.uniqundio.tupataamiga.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    fun mostrarSnackbar(mensaje: String) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(message = mensaje)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = backgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = LocaleManager.tr("Crear cuenta", "Create Account"),
                        fontWeight = FontWeight.Bold,
                        color = mainTextColor,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = mainTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundColor
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, brandGreen, CircleShape)
                    .background(fieldBg)
                    .clickable {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(LocaleManager.tr("Foto seleccionada", "Photo selected"))
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Cargar foto",
                    tint = brandGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = LocaleManager.tr("Nombre completo", "Full name"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextColor
                    )
                    TextField(
                        value = uiState.nombre,
                        onValueChange = viewModel::onNombreChanged,
                        placeholder = { Text("María Gómez") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.nombreError != null,
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
                    uiState.nombreError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = LocaleManager.tr("Correo electrónico", "Email address"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextColor
                    )
                    TextField(
                        value = uiState.correo,
                        onValueChange = viewModel::onCorreoChanged,
                        placeholder = { Text("ejemplo@uniqundio.edu.co") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.correoError != null,
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
                    uiState.correoError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = LocaleManager.tr("Contraseña", "Password"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextColor
                    )
                    TextField(
                        value = uiState.clave,
                        onValueChange = viewModel::onClaveChanged,
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.claveError != null,
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
                    uiState.claveError?.let {
                        Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = LocaleManager.tr("Dirección de residencia", "Residential address"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = subTextColor
                    )
                    TextField(
                        value = uiState.direccion,
                        onValueChange = viewModel::onDireccionChanged,
                        placeholder = { Text("Armenia, Quindío") },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Mapa",
                                tint = brandGreen
                            )
                        },
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = fieldBg,
                            unfocusedContainerColor = fieldBg,
                            disabledContainerColor = fieldBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.register(
                        onSuccess = {
                            mostrarSnackbar(LocaleManager.tr("¡Registro exitoso!", "Registration successful!"))
                            onRegisterSuccess()
                        },
                        onError = { msg ->
                            mostrarSnackbar(msg)
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                enabled = !uiState.isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                shape = RoundedCornerShape(23.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = LocaleManager.tr("REGISTRARME", "REGISTER"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
