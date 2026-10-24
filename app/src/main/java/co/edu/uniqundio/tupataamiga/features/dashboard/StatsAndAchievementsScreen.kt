package co.edu.uniqundio.tupataamiga.features.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsAndAchievementsScreen(
    onBack: () -> Unit = {}
) {
    val brandGreen = Color(0xFF0F4C25)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Estadísticas y Logros",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 20.sp
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFDFBF7)
                )
            )
        },
        containerColor = Color(0xFFFDFBF7)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFFDFBF7))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Grid de 4 Tarjetas de Métricas (2x2)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Campaign,
                        number = "3",
                        label = "Activas",
                        bgColor = Color(0xFFFDEAE1),
                        textColor = Color(0xFFC62828)
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.CheckCircle,
                        number = "4",
                        label = "Verificadas",
                        bgColor = Color(0xFFE5F7ED),
                        textColor = Color(0xFF2E7D32)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.HourglassEmpty,
                        number = "2",
                        label = "Pendientes",
                        bgColor = Color(0xFFFFF6E5),
                        textColor = Color(0xFFE65100)
                    )
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Flag,
                        number = "1",
                        label = "Finalizadas",
                        bgColor = Color(0xFFF2ECE9),
                        textColor = Color(0xFF616161)
                    )
                }
            }

            // 2. Card de Nivel Actual y Barra de Progreso
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDEAE1))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "NIVEL ACTUAL",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Pets,
                                    contentDescription = null,
                                    tint = brandGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Amigo Animal",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "142",
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp,
                                color = brandGreen
                            )
                            Text(text = "puntos", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Amigo Animal", fontSize = 11.sp, color = Color.Gray)
                        Text(text = "Protector (200 pts)", fontSize = 11.sp, color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { 0.71f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = brandGreen,
                        trackColor = Color.White.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "58 puntos para el siguiente nivel",
                        fontSize = 12.sp,
                        color = brandGreen,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 3. NIVELES (Row de Niveles)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "NIVELES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LevelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Pets,
                        title = "Amigo Animal",
                        pts = "0+",
                        isSelected = true,
                        brandGreen = brandGreen
                    )
                    LevelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Shield,
                        title = "Protector",
                        pts = "200+",
                        isSelected = false,
                        brandGreen = brandGreen
                    )
                    LevelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Star,
                        title = "Guardián",
                        pts = "500+",
                        isSelected = false,
                        brandGreen = brandGreen
                    )
                    LevelCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Person,
                        title = "Héroe",
                        pts = "1000+",
                        isSelected = false,
                        brandGreen = brandGreen
                    )
                }
            }

            // 4. INSIGNIAS (Grid de Insignias y Logros)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "INSIGNIAS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 0.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BadgeCard(
                        modifier = Modifier.weight(1f),
                        title = "Primera Huella",
                        sub = "1ª publicación",
                        icon = Icons.Default.Pets,
                        bgColor = Color(0xFFFFF6E5),
                        iconTint = brandGreen,
                        isUnlocked = true
                    )
                    BadgeCard(
                        modifier = Modifier.weight(1f),
                        title = "Rescatador",
                        sub = "5 publicaciones verificadas",
                        icon = Icons.Default.CheckCircle,
                        bgColor = Color(0xFFE5F7ED),
                        iconTint = Color(0xFF2E7D32),
                        isUnlocked = true
                    )
                    BadgeCard(
                        modifier = Modifier.weight(1f),
                        title = "Corazón Generoso",
                        sub = "3 adopciones",
                        icon = Icons.Default.Favorite,
                        bgColor = Color(0xFFFDEAE1),
                        iconTint = Color(0xFFE53935),
                        isUnlocked = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    BadgeCard(
                        modifier = Modifier.weight(1f),
                        title = "Del Mes",
                        sub = "Pub. destacada",
                        icon = Icons.Default.Whatshot,
                        bgColor = Color(0xFFF5ECE8),
                        iconTint = Color.Gray,
                        isUnlocked = false
                    )
                    BadgeCard(
                        modifier = Modifier.weight(1f),
                        title = "Héroe de Mascotas",
                        sub = "1000 pts",
                        icon = Icons.Default.Person,
                        bgColor = Color(0xFFF5ECE8),
                        iconTint = Color.Gray,
                        isUnlocked = false
                    )
                    BadgeCard(
                        modifier = Modifier.weight(1f),
                        title = "Comentarista",
                        sub = "50 comentarios",
                        icon = Icons.Default.Comment,
                        bgColor = Color(0xFFF5ECE8),
                        iconTint = Color.Gray,
                        isUnlocked = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier,
    icon: ImageVector,
    number: String,
    label: String,
    bgColor: Color,
    textColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = number,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun LevelCard(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    pts: String,
    isSelected: Boolean,
    brandGreen: Color
) {
    Card(
        modifier = modifier
            .then(
                if (isSelected) Modifier.border(2.dp, brandGreen, RoundedCornerShape(18.dp)) else Modifier
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFDEAE1) else Color(0xFFF5ECE8)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 14.dp, horizontal = 4.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) brandGreen else Color(0xFF1976D2),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = pts,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun BadgeCard(
    modifier: Modifier,
    title: String,
    sub: String,
    icon: ImageVector,
    bgColor: Color,
    iconTint: Color,
    isUnlocked: Boolean
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isUnlocked) iconTint else Color.Gray.copy(alpha = 0.6f),
                modifier = Modifier.size(26.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isUnlocked) Color.Black else Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = sub,
                fontSize = 10.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isUnlocked) {
                Surface(
                    color = Color(0xFF4CAF50),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "✓ Lograda",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
