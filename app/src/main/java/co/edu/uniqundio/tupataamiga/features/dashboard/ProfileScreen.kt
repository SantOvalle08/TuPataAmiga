package co.edu.uniqundio.tupataamiga.features.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogoutClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)

    var showStatsScreen by remember { mutableStateOf(false) }
    var showModerationPanel by remember { mutableStateOf(false) }

    // Dialog states for interactive profile buttons
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showMyPublicationsDialog by remember { mutableStateOf(false) }
    var showMyInterestsDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Notification Permission Launcher for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scope.launch {
                snackbarHostState.showSnackbar("Permiso de notificaciones concedido")
            }
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Permiso de notificaciones denegado")
            }
        }
    }

    if (showModerationPanel) {
        ModerationPanelScreen(
            onBack = { showModerationPanel = false }
        )
    } else if (showStatsScreen) {
        StatsAndAchievementsScreen(
            onBack = { showStatsScreen = false }
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Mi Perfil",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 22.sp
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Inicia sesión para editar tu perfil")
                                    }
                                } else {
                                    showEditProfileDialog = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = brandGreen
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFF6FBF7)
                    )
                )
            },
            containerColor = Color(0xFFF6FBF7)
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF6FBF7))
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // User Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = fieldBg)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Circle
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(brandGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = uiState.nombreMostrar,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = uiState.correoMostrar,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Rank Badge
                                Surface(
                                    color = brandGreen,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.clickable {
                                        if (uiState.esInvitado) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Regístrate para ganar insignias")
                                            }
                                        } else {
                                            showStatsScreen = true
                                        }
                                    }
                                ) {
                                    Text(
                                        text = uiState.rangoMostrar,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(16.dp))

                        // 3 Stats Columns
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (uiState.esInvitado) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Regístrate para ver tus estadísticas acumuladas")
                                        }
                                    } else {
                                        showStatsScreen = true
                                    }
                                },
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(number = "${uiState.publicacionesContador}", label = "Publicaciones")
                            ProfileStatItem(number = "${uiState.comentariosContador}", label = "Comentarios")
                            ProfileStatItem(number = "${uiState.puntosContador}", label = "Puntos")
                        }
                    }
                }

                // Menu Items List Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.AdminPanelSettings,
                            iconTint = brandGreen,
                            title = "Panel de Moderación",
                            countBadge = if (uiState.esModerador) "3" else null,
                            onClick = {
                                if (uiState.esModerador) {
                                    showModerationPanel = true
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("El panel de moderación solo está disponible para moderadores de TuPataAmiga")
                                    }
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            title = "Mis publicaciones",
                            countBadge = if (!uiState.esInvitado) "${uiState.publicacionesContador}" else null,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para gestionar tus publicaciones") }
                                } else {
                                    showMyPublicationsDialog = true
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Favorite,
                            iconTint = Color(0xFFE53935),
                            title = "Mis intereses en adopción",
                            countBadge = if (!uiState.esInvitado) "3" else null,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para ver tus mascotas guardadas") }
                                } else {
                                    showMyInterestsDialog = true
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.EmojiEvents,
                            iconTint = Color(0xFFF57F17),
                            title = "Logros e insignias",
                            countBadge = if (!uiState.esInvitado) "4" else null,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para desbloquear insignias") }
                                } else {
                                    showStatsScreen = true
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.BarChart,
                            title = "Estadísticas personales",
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para acumular estadísticas") }
                                } else {
                                    showStatsScreen = true
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Configurar notificaciones",
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasNotifPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasNotifPerm) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                                showNotificationsDialog = true
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            title = "Editar datos personales",
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para editar tu perfil") }
                                } else {
                                    showEditProfileDialog = true
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        // Destructive Options
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = if (uiState.esInvitado) "Iniciar sesión / Registrarse" else "Cerrar sesión",
                            isDestructive = !uiState.esInvitado,
                            onClick = {
                                if (uiState.esInvitado) {
                                    viewModel.logout()
                                    onLogoutClick()
                                } else {
                                    showLogoutDialog = true
                                }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Delete,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = "Eliminar cuenta",
                            isDestructive = true,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("No hay una cuenta activa para eliminar") }
                                } else {
                                    showDeleteAccountDialog = true
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // 1. EDIT PROFILE DIALOG
    if (showEditProfileDialog) {
        var editNombre by remember { mutableStateOf(uiState.nombreMostrar) }
        var editCorreo by remember { mutableStateOf(uiState.correoMostrar) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Editar datos personales", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editNombre,
                        onValueChange = { editNombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCorreo,
                        onValueChange = { editCorreo = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showEditProfileDialog = false
                        viewModel.updateUserProfile(editNombre, editCorreo) { _, message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // 2. CONFIGURAR NOTIFICACIONES DIALOG
    if (showNotificationsDialog) {
        var pushEnabled by remember { mutableStateOf(true) }
        var alertMascotasEnabled by remember { mutableStateOf(true) }
        var comentariosEnabled by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Configurar notificaciones", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notificaciones Push", fontSize = 14.sp)
                        Switch(
                            checked = pushEnabled,
                            onCheckedChange = {
                                pushEnabled = it
                                if (it && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasNotifPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasNotifPerm) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Alertas de mascotas perdidas", fontSize = 14.sp)
                        Switch(
                            checked = alertMascotasEnabled,
                            onCheckedChange = { alertMascotasEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nuevos comentarios e intereses", fontSize = 14.sp)
                        Switch(
                            checked = comentariosEnabled,
                            onCheckedChange = { comentariosEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showNotificationsDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Preferencias de notificación guardadas")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // 3. MIS PUBLICACIONES DIALOG
    if (showMyPublicationsDialog) {
        AlertDialog(
            onDismissRequest = { showMyPublicationsDialog = false },
            title = { Text("Mis publicaciones", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tienes ${uiState.publicacionesContador} publicaciones activas en la plataforma:", fontSize = 13.sp)
                    HorizontalDivider()
                    Text("• Rex busca hogar (Adopción)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("• Michi extraviado (Perdidos)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showMyPublicationsDialog = false }) {
                    Text("Entendido", color = brandGreen)
                }
            }
        )
    }

    // 4. MIS INTERESES DIALOG
    if (showMyInterestsDialog) {
        AlertDialog(
            onDismissRequest = { showMyInterestsDialog = false },
            title = { Text("Mis intereses en adopción", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Mascotas en las que has mostrado interés:", fontSize = 13.sp)
                    HorizontalDivider()
                    Text("• Firulais (Perro - Adopción)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("• Pelusa (Gato - Adopción)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("• Max (Perro - Encontrado)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showMyInterestsDialog = false }) {
                    Text("Entendido", color = brandGreen)
                }
            }
        )
    }

    // 5. CERRAR SESIÓN DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("Regresarás a la pantalla de inicio.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // 6. ELIMINAR CUENTA DIALOG
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("¿Eliminar cuenta permanentemente?") },
            text = { Text("Esta acción removerá tu cuenta y tus publicaciones de la plataforma.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccount {
                            onLogoutClick()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ProfileStatItem(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconTint: Color = Color.DarkGray,
    title: String,
    countBadge: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error else Color.Black
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (countBadge != null) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = CircleShape
                ) {
                    Text(
                        text = countBadge,
                        color = Color(0xFFC62828),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
