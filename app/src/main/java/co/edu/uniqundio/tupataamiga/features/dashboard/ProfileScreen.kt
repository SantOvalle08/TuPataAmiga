package co.edu.uniqundio.tupataamiga.features.dashboard

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
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
    onLogoutClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val myPublications by viewModel.myPublications.collectAsState()
    val adoptionInterests by viewModel.adoptionInterests.collectAsState()
    val context = LocalContext.current

    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val cardBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    var showStatsScreen by remember { mutableStateOf(value = false) }
    var showModerationPanel by remember { mutableStateOf(value = false) }
    var showMyPublicationsScreen by remember { mutableStateOf(value = false) }
    var showMyInterestsScreen by remember { mutableStateOf(value = false) }
    var showSettingsScreen by remember { mutableStateOf(value = false) }

    // Dialog states for interactive profile buttons
    var showEditProfileDialog by remember { mutableStateOf(value = false) }
    var showNotificationsDialog by remember { mutableStateOf(value = false) }
    var showLogoutDialog by remember { mutableStateOf(value = false) }
    var showDeleteAccountDialog by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
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

    if (showSettingsScreen) {
        AppSettingsFullScreen(
            onBack = { showSettingsScreen = false },
            brandGreen = brandGreen,
            fieldBg = fieldBg
        )
    } else if (showMyPublicationsScreen) {
        MyPublicationsFullScreen(
            onBack = { showMyPublicationsScreen = false },
            brandGreen = brandGreen,
            fieldBg = fieldBg,
            mascotas = myPublications,
        )
    } else if (showMyInterestsScreen) {
        MyAdoptionInterestsFullScreen(
            onBack = { showMyInterestsScreen = false },
            brandGreen = brandGreen,
            fieldBg = fieldBg,
            mascotas = adoptionInterests,
        )
    } else if (showModerationPanel) {
        ModerationPanelScreen(
            onBack = { showModerationPanel = false },
        )
    } else if (showStatsScreen) {
        StatsAndAchievementsScreen(
            onBack = { showStatsScreen = false },
            profileState = uiState,
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Mi Perfil", "My Profile"),
                            fontWeight = FontWeight.Bold,
                            color = mainTextColor,
                            fontSize = 22.sp,
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
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = brandGreen,
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = backgroundColor,
                    ),
                )
            },
            containerColor = backgroundColor,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(backgroundColor)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // User Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = fieldBg),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Avatar Circle
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(brandGreen),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (!uiState.usuario?.fotoUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = uiState.usuario?.fotoUrl,
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(36.dp),
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    text = uiState.nombreMostrar,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = mainTextColor,
                                )
                                Text(
                                    text = uiState.correoMostrar,
                                    fontSize = 13.sp,
                                    color = subTextColor,
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
                                    },
                                ) {
                                    Text(
                                        text = uiState.rangoMostrar,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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
                            horizontalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            ProfileStatItem(number = uiState.publicacionesContador.toString(), label = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Publicaciones", "Posts"))
                            ProfileStatItem(number = uiState.comentariosContador.toString(), label = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Comentarios", "Comments"))
                            ProfileStatItem(number = uiState.puntosContador.toString(), label = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Puntos", "Points"))
                        }
                    }
                }

                // Menu Items List Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Default.AdminPanelSettings,
                            iconTint = brandGreen,
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Panel de Moderación", "Moderation Panel"),
                            countBadge = if (uiState.esModerador) uiState.pendientesContador.toString() else null,
                            onClick = {
                                if (uiState.esModerador) {
                                    showModerationPanel = true
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("El panel de moderación solo está disponible para moderadores de TuPataAmiga")
                                    }
                                }
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Mis publicaciones", "My Posts"),
                            countBadge = if (!uiState.esInvitado) uiState.publicacionesContador.toString() else null,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para gestionar tus publicaciones") }
                                } else {
                                    showMyPublicationsScreen = true
                                }
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Favorite,
                            iconTint = Color(0xFFE53935),
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Mis intereses en adopción", "My Adoption Interests"),
                            countBadge = if (!uiState.esInvitado) uiState.interesesContador.toString() else null,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para ver tus mascotas guardadas") }
                                } else {
                                    showMyInterestsScreen = true
                                }
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.EmojiEvents,
                            iconTint = Color(0xFFF57F17),
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Logros e insignias", "Achievements & Badges"),
                            countBadge = if (!uiState.esInvitado) uiState.insigniasDesbloqueadas.toString() else null,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para desbloquear insignias") }
                                } else {
                                    showStatsScreen = true
                                }
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.BarChart,
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Estadísticas personales", "Personal Statistics"),
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para acumular estadísticas") }
                                } else {
                                    showStatsScreen = true
                                }
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Configurar notificaciones", "Notification Settings"),
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val hasNotifPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS,
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasNotifPerm) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                                showNotificationsDialog = true
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Ajustes de la aplicación", "App Settings"),
                            onClick = {
                                showSettingsScreen = true
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Person,
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Editar datos personales", "Edit Personal Data"),
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("Inicia sesión para editar tu perfil") }
                                } else {
                                    showEditProfileDialog = true
                                }
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        // Destructive Options
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = if (uiState.esInvitado) co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Iniciar sesión / Registrarse", "Sign In / Sign Up") else co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Cerrar sesión", "Log Out"),
                            isDestructive = !uiState.esInvitado,
                            onClick = {
                                if (uiState.esInvitado) {
                                    viewModel.logout()
                                    onLogoutClick()
                                } else {
                                    showLogoutDialog = true
                                }
                            },
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Delete,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Eliminar cuenta", "Delete Account"),
                            isDestructive = true,
                            onClick = {
                                if (uiState.esInvitado) {
                                    scope.launch { snackbarHostState.showSnackbar("No hay una cuenta activa para eliminar") }
                                } else {
                                    showDeleteAccountDialog = true
                                }
                            },
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
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = editCorreo,
                        onValueChange = { editCorreo = it },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
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
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    // 2. CONFIGURAR NOTIFICACIONES DIALOG
    if (showNotificationsDialog) {
        var pushEnabled by remember { mutableStateOf(value = true) }
        var alertMascotasEnabled by remember { mutableStateOf(value = true) }
        var comentariosEnabled by remember { mutableStateOf(value = false) }

        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = { Text("Configurar notificaciones", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Notificaciones Push", fontSize = 14.sp)
                        Switch(
                            checked = pushEnabled,
                            onCheckedChange = { isChecked ->
                                pushEnabled = isChecked
                                if (isChecked && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)) {
                                    val hasNotifPerm = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.POST_NOTIFICATIONS,
                                    ) == PackageManager.PERMISSION_GRANTED

                                    if (!hasNotifPerm) {
                                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Alertas de mascotas perdidas", fontSize = 14.sp)
                        Switch(
                            checked = alertMascotasEnabled,
                            onCheckedChange = { alertMascotasEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen),
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Nuevos comentarios e intereses", fontSize = 14.sp)
                        Switch(
                            checked = comentariosEnabled,
                            onCheckedChange = { comentariosEnabled = it },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen),
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
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    // 3. CERRAR SESIÓN DIALOG
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }

    // 4. ELIMINAR CUENTA DIALOG
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
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

// Full Screen View: Mis Publicaciones
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyPublicationsFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color,
    mascotas: List<co.edu.uniqundio.tupataamiga.domain.model.Mascota>,
) {
    var selectedFilter by remember { mutableStateOf("Todas") }

    val myPostsList = mascotas.map { mascota ->
        MyPostUiModel(
            id = mascota.id,
            nombre = mascota.nombre,
            tipo = mascota.categoria.name,
            estadoModeracion = when (mascota.estado) {
                co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.APROBADO -> "Verificada"
                co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.PENDIENTE -> "Pendiente"
                co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.FINALIZADO -> "Finalizada"
                co.edu.uniqundio.tupataamiga.domain.model.EstadoPublicacion.RECHAZADO -> "Rechazada"
            },
            ubicacion = mascota.ubicacion,
            interaccion = "${mascota.interesAdopcionCount} interesados · ${mascota.comentarios.size} comentarios",
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Assignment,
                            contentDescription = null,
                            tint = brandGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mis Publicaciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6FBF7))
            )
        },
        containerColor = Color(0xFFF6FBF7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
                .padding(horizontal = 16.dp)
        ) {
            // Header Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = fieldBg)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = null,
                        tint = brandGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Gestión de tus 7 publicaciones",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = brandGreen
                        )
                        Text(
                            text = "Aquí puedes revisar el estado de aprobación de tus mascotas, editarlas o cambiar su estado.",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips Row
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Todas", "Activas", "Verificadas", "Pendientes", "Finalizadas")) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = brandGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Publications List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(myPostsList, key = { it.id }) { post ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(fieldBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = null,
                                        tint = brandGreen,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = if (post.estadoModeracion == "Verificada") Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = if (post.estadoModeracion == "Verificada") Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
                                                    contentDescription = null,
                                                    tint = if (post.estadoModeracion == "Verificada") Color(0xFF2E7D32) else Color(0xFFF57F17),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = post.estadoModeracion,
                                                    color = if (post.estadoModeracion == "Verificada") Color(0xFF2E7D32) else Color(0xFFF57F17),
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }

                                        Text(text = post.tipo, fontSize = 11.sp, color = brandGreen, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = post.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(text = post.ubicacion, fontSize = 11.sp, color = Color.Gray)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = post.interaccion, fontSize = 11.sp, color = Color.Gray)

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { },
                                        shape = RoundedCornerShape(16.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                    ) {
                                        Text(co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Editar", "Edit"), fontSize = 11.sp, color = brandGreen)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Full Screen View: Mis Intereses en Adopción
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyAdoptionInterestsFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color,
    mascotas: List<co.edu.uniqundio.tupataamiga.domain.model.Mascota>,
) {
    val interestsList = mascotas.map { mascota ->
        MyInterestUiModel(
            id = mascota.id,
            nombre = mascota.nombre,
            raza = "${mascota.tipoAnimal.name} · ${mascota.raza}",
            dueno = mascota.nombrePublicante,
            ubicacion = mascota.ubicacion,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Mis Intereses en Adopción", "My Adoption Interests"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF6FBF7))
            )
        },
        containerColor = Color(0xFFF6FBF7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF6FBF7))
                .padding(horizontal = 16.dp)
        ) {
            // Header Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "${interestsList.size} Mascotas Guardadas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFFC62828)
                        )
                        Text(
                            text = "Mascotas que has marcado con interés. Ponte en contacto con sus rescatistas para coordinar la adopción.",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interests List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(interestsList, key = { it.id }) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(fieldBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Pets,
                                        contentDescription = null,
                                        tint = brandGreen,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.nombre,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = item.raza, fontSize = 12.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "Publicante: ${item.dueno}", fontSize = 11.sp, color = brandGreen, fontWeight = FontWeight.Medium)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(text = item.ubicacion, fontSize = 11.sp, color = Color.Gray)
                                }

                                Button(
                                    onClick = { },
                                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                                    shape = RoundedCornerShape(16.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text(co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.tr("Contactar", "Contact"), fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileStatItem(number: String, label: String) {
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = mainTextColor,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = subTextColor,
        )
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconTint: Color = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor,
    title: String,
    countBadge: String? = null,
    isDestructive: Boolean = false,
    onClick: () -> Unit,
) {
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error else mainTextColor,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (countBadge != null) {
                Surface(
                    color = Color(0xFFFFEBEE),
                    shape = CircleShape,
                ) {
                    Text(
                        text = countBadge,
                        color = Color(0xFFC62828),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

private data class MyPostUiModel(
    val id: String,
    val nombre: String,
    val tipo: String,
    val estadoModeracion: String,
    val ubicacion: String,
    val interaccion: String
)

private data class MyInterestUiModel(
    val id: String,
    val nombre: String,
    val raza: String,
    val dueno: String,
    val ubicacion: String
)

// Full Screen View: Ajustes de la Aplicación
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppSettingsFullScreen(
    onBack: () -> Unit,
    brandGreen: Color,
    fieldBg: Color
) {
    val context = LocalContext.current
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    var isDarkMode by remember { mutableStateOf(co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.isDarkTheme.value) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isEnglish = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.currentLanguage.value == "en"

    val titleText = if (isEnglish) "App Settings" else "Ajustes de la Aplicación"
    val appearanceSection = if (isEnglish) "APPEARANCE & THEME" else "APARIENCIA Y TEMA"
    val darkModeTitle = if (isEnglish) "Dark Mode" else "Modo Oscuro"
    val darkModeDesc = if (isDarkMode) (if (isEnglish) "Enabled (Dark Background · White Text)" else "Activado (Fondo Oscuro · Texto Blanco)") else (if (isEnglish) "Disabled (Light Mode)" else "Desactivado (Modo Claro)")
    val languageTitle = if (isEnglish) "App Language" else "Idioma de la Aplicación"
    val soundSection = if (isEnglish) "SOUND & VIBRATION" else "SONIDO Y VIBRACIÓN"
    val alertSoundTitle = if (isEnglish) "Alert sound effects" else "Efectos de sonido de alertas"
    val alertSoundDesc = if (soundEnabled) (if (isEnglish) "Sounds enabled" else "Sonidos activados") else (if (isEnglish) "Muted" else "Sonidos silenciados")
    val vibrationTitle = if (isEnglish) "Notification vibration" else "Vibración de notificaciones"
    val vibrationDesc = if (vibrationEnabled) (if (isEnglish) "Vibration enabled" else "Vibración activada") else (if (isEnglish) "Vibration disabled" else "Vibración desactivada")
    val aboutSection = if (isEnglish) "ABOUT & LEGAL" else "ACERCA DE Y LEGAL"
    val termsTitle = if (isEnglish) "Terms and Conditions of Use" else "Términos y condiciones de uso"
    val privacyTitle = if (isEnglish) "Privacy Policy" else "Política de privacidad"
    val aboutTitle = if (isEnglish) "PataAmiga for Android" else "PataAmiga para Android"
    val aboutSub = if (isEnglish) "Version 1.0.0 · Universidad del Quindío 2026" else "Versión 1.0.0 · Universidad del Quindío 2026"

    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val cardBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = brandGreen,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = titleText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = mainTextColor
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Apariencia
            Text(text = appearanceSection, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = subTextColor, letterSpacing = 0.5.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(darkModeTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = mainTextColor)
                            Text(
                                text = darkModeDesc,
                                fontSize = 12.sp,
                                color = subTextColor
                            )
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { isChecked ->
                                isDarkMode = isChecked
                                co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.isDarkTheme.value = isChecked
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        if (isChecked) (if (isEnglish) "Dark mode enabled" else "Modo oscuro activado en toda la aplicación") else (if (isEnglish) "Light mode enabled" else "Modo claro activado")
                                    )
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(languageTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = mainTextColor)
                            Text(
                                text = if (co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.currentLanguage.value == "en") "English" else "Español (Colombia)",
                                fontSize = 12.sp,
                                color = brandGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.currentLanguage.value == "es",
                                onClick = {
                                    co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.setLanguage(context, "es")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Idioma cambiado a Español")
                                    }
                                },
                                label = { Text("ES", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = brandGreen,
                                    selectedLabelColor = Color.White
                                )
                            )

                            FilterChip(
                                selected = co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.currentLanguage.value == "en",
                                onClick = {
                                    co.edu.uniqundio.tupataamiga.ui.theme.LocaleManager.setLanguage(context, "en")
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Language changed to English")
                                    }
                                },
                                label = { Text("EN", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = brandGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Section 2: Notificaciones y Sonidos
            Text(text = soundSection, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = subTextColor, letterSpacing = 0.5.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(alertSoundTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = mainTextColor)
                            Text(alertSoundDesc, fontSize = 12.sp, color = subTextColor)
                        }
                        Switch(
                            checked = soundEnabled,
                            onCheckedChange = {
                                soundEnabled = it
                                scope.launch {
                                    snackbarHostState.showSnackbar(if (it) (if (isEnglish) "Alert sounds enabled" else "Sonidos de alerta activados") else (if (isEnglish) "Muted" else "Sonidos silenciados"))
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen)
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(vibrationTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = mainTextColor)
                            Text(vibrationDesc, fontSize = 12.sp, color = subTextColor)
                        }
                        Switch(
                            checked = vibrationEnabled,
                            onCheckedChange = {
                                vibrationEnabled = it
                                if (it) {
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                }
                                scope.launch {
                                    snackbarHostState.showSnackbar(if (it) (if (isEnglish) "Vibration enabled" else "Vibración activada") else (if (isEnglish) "Vibration disabled" else "Vibración desactivada"))
                                }
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = brandGreen)
                        )
                    }
                }
            }

            // Section 3: Información y Legal
            Text(text = aboutSection, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = subTextColor, letterSpacing = 0.5.sp)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTermsDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(termsTitle, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = mainTextColor)
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showPrivacyDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(privacyTitle, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = mainTextColor)
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAboutDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(aboutTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = mainTextColor)
                            Text(aboutSub, fontSize = 12.sp, color = subTextColor)
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Términos y Condiciones", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Bienvenido a PataAmiga. Al hacer uso de esta aplicación aceptas promover la adopción responsable de mascotas, garantizar información verídica en las publicaciones y respetar las normas comunitarias establecidas por la Universidad del Quindío.",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showTermsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Aceptar", color = Color.White)
                }
            }
        )
    }

    // Privacy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Política de Privacidad", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Tus datos personales y de contacto en PataAmiga se gestionan de manera segura únicamente con el fin de facilitar la adopción, rescate y moderación de publicaciones de mascotas en Colombia.",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPrivacyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Entendido", color = Color.White)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("Acerca de PataAmiga", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🐾 PataAmiga v1.0.0", fontWeight = FontWeight.Bold, color = brandGreen)
                    Text("Red de Adopción y Rescate de Mascotas en Colombia.", fontSize = 13.sp, color = Color.DarkGray)
                    Text("Desarrollado para la asignatura de Desarrollo de Aplicaciones Móviles.", fontSize = 12.sp, color = Color.Gray)
                    Text("Universidad del Quindío · 2026", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = brandGreen)
                ) {
                    Text("Cerrar", color = Color.White)
                }
            }
        )
    }
}
