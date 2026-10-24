package co.edu.uniqundio.tupataamiga.features.dashboard

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit = {}
) {
    val brandGreen = Color(0xFF0F4C25)
    val fieldBg = Color(0xFFE8F5E9)

    var showStatsScreen by remember { mutableStateOf(false) }
    var showModerationPanel by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

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
                                scope.launch {
                                    snackbarHostState.showSnackbar("Editar perfil")
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
                                    text = "María Gómez",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                                Text(
                                    text = "maria.gomez@gmail.com",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                // Rank Badge
                                Surface(
                                    color = brandGreen,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.clickable { showStatsScreen = true }
                                ) {
                                    Text(
                                        text = "🐾 Amigo Animal",
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
                                .clickable { showStatsScreen = true },
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(number = "7", label = "Publicaciones")
                            ProfileStatItem(number = "23", label = "Comentarios")
                            ProfileStatItem(number = "142", label = "Puntos")
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
                            countBadge = "3",
                            onClick = { showModerationPanel = true }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            title = "Mis publicaciones",
                            countBadge = "7",
                            onClick = {
                                scope.launch { snackbarHostState.showSnackbar("Mis publicaciones (7)") }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Favorite,
                            iconTint = Color(0xFFE53935),
                            title = "Mis intereses en adopción",
                            countBadge = "3",
                            onClick = {
                                scope.launch { snackbarHostState.showSnackbar("Mis intereses en adopción (3)") }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.EmojiEvents,
                            iconTint = Color(0xFFF57F17),
                            title = "Logros e insignias",
                            countBadge = "4",
                            onClick = { showStatsScreen = true }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.BarChart,
                            title = "Estadísticas personales",
                            onClick = { showStatsScreen = true }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Notifications,
                            title = "Configurar notificaciones",
                            onClick = {
                                scope.launch { snackbarHostState.showSnackbar("Configurar notificaciones") }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            title = "Editar datos personales",
                            onClick = {
                                scope.launch { snackbarHostState.showSnackbar("Editar datos personales") }
                            }
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        // Destructive Options
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = "Cerrar sesión",
                            isDestructive = true,
                            onClick = onLogoutClick
                        )
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))

                        ProfileMenuItem(
                            icon = Icons.Default.Delete,
                            iconTint = MaterialTheme.colorScheme.error,
                            title = "Eliminar cuenta",
                            isDestructive = true,
                            onClick = {
                                scope.launch { snackbarHostState.showSnackbar("Eliminar cuenta") }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
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
