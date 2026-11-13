package co.edu.uniqundio.tupataamiga.features.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.preference.PreferenceManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import co.edu.uniqundio.tupataamiga.ui.theme.abrirComoLlegar
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class MapPetMarker(
    val id: String,
    val nombre: String,
    val categoria: String,
    val lugar: String,
    val lat: Double,
    val lng: Double,
    val distancia: String,
    val markerColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onMascotaClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val backgroundColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.backgroundColor
    val cardBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.cardColor
    val mainTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.textColor
    val subTextColor = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.subtextColor
    val brandGreen = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.brandGreenColor
    val fieldBg = co.edu.uniqundio.tupataamiga.ui.theme.ThemeManager.fieldColor

    var selectedFilter by remember { mutableStateOf("Todos") }

    val petMarkers = remember {
        listOf(
            MapPetMarker("1", "Rex - Labrador", "Adopción", "El Bosque, Armenia", 4.5389, -75.6781, "a 1.2 km", Color(0xFF2E7D32)),
            MapPetMarker("2", "Michi - Siamés", "Perdidos", "Centro, Armenia", 4.5338, -75.6811, "a 0.5 km", Color(0xFFC62828)),
            MapPetMarker("3", "Perrito Mestizo", "Encontrados", "Plaza de Bolívar", 4.5321, -75.6834, "a 0.8 km", Color(0xFF1565C0)),
            MapPetMarker("4", "Gata Persa", "Hogar Temporal", "La Castellana", 4.5456, -75.6723, "a 2.4 km", Color(0xFFF57F17)),
            MapPetMarker("5", "Toby - Beagle", "Adopción", "Granada, Armenia", 4.5412, -75.6750, "a 1.8 km", Color(0xFF2E7D32))
        )
    }

    val filteredMarkers = remember(selectedFilter, petMarkers) {
        if (selectedFilter == "Todos") petMarkers
        else petMarkers.filter { it.categoria == selectedFilter }
    }

    var selectedMarker by remember { mutableStateOf<MapPetMarker?>(petMarkers.first()) }

    val pawBitmap = rememberVectorToBitmap(
        imageVector = Icons.Default.Pets,
        tint = Color.White,
        sizeDp = 20
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = brandGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mapa de Mascotas",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = mainTextColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor)
            )
        },
        containerColor = backgroundColor
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(backgroundColor)
        ) {
            // Mapa real de OpenStreetMap (Armenia, Quindío)
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    Configuration.getInstance().load(
                        ctx,
                        PreferenceManager.getDefaultSharedPreferences(ctx)
                    )
                    Configuration.getInstance().userAgentValue = ctx.packageName

                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(14.5)
                        controller.setCenter(GeoPoint(4.5375, -75.6780))
                    }
                },
                update = { mapView ->
                    mapView.overlays.clear()

                    filteredMarkers.forEach { pet ->
                        val isSelected = selectedMarker?.id == pet.id
                        val pinColor = if (isSelected) brandGreen else pet.markerColor

                        val marker = Marker(mapView).apply {
                            position = GeoPoint(pet.lat, pet.lng)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                            icon = createCircularPawDrawable(
                                context = mapView.context,
                                backgroundColor = pinColor.toArgb(),
                                pawBitmap = pawBitmap,
                                isSelected = isSelected
                            )
                            setOnMarkerClickListener { _, _ ->
                                selectedMarker = pet
                                mapView.controller.animateTo(GeoPoint(pet.lat, pet.lng))
                                true
                            }
                        }
                        mapView.overlays.add(marker)
                    }
                    mapView.invalidate()
                }
            )

            // Superposición superior: Filtros + Píldora de ubicación
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("Todos", "Adopción", "Perdidos", "Encontrados", "Hogar Temporal")) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = {
                                selectedFilter = filter
                                val nuevas = if (filter == "Todos") petMarkers else petMarkers.filter { it.categoria == filter }
                                if (nuevas.none { it.id == selectedMarker?.id }) {
                                    selectedMarker = nuevas.firstOrNull()
                                }
                            },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White,
                                selectedContainerColor = brandGreen,
                                selectedLabelColor = Color.White
                            ),
                            elevation = FilterChipDefaults.filterChipElevation(elevation = 3.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            tint = brandGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Armenia, Quindío",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Tarjeta inferior de la mascota seleccionada con botón "Cómo llegar" y "Ver"
            selectedMarker?.let { marker ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(fieldBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = brandGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = marker.markerColor.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = marker.categoria,
                                        color = marker.markerColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                Text(
                                    text = marker.distancia,
                                    fontSize = 11.sp,
                                    color = brandGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = marker.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = mainTextColor
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = subTextColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = marker.lugar,
                                    fontSize = 12.sp,
                                    color = subTextColor
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Fila con botón rápido "Cómo llegar" + botón "Ver"
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilledTonalIconButton(
                                onClick = { abrirComoLlegar(context, marker.lat, marker.lng, marker.nombre) },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = fieldBg,
                                    contentColor = brandGreen
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = "Cómo llegar",
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Button(
                                onClick = { onMascotaClick(marker.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = brandGreen),
                                shape = RoundedCornerShape(14.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text("Ver", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberVectorToBitmap(
    imageVector: ImageVector,
    tint: Color,
    sizeDp: Int
): Bitmap {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val painter = rememberVectorPainter(imageVector)
    val sizePx = with(density) { sizeDp.dp.toPx().toInt() }

    return remember(imageVector, tint, sizePx) {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val androidCanvas = Canvas(bitmap)
        val composeCanvas = androidx.compose.ui.graphics.Canvas(androidCanvas)
        val drawScope = CanvasDrawScope()

        drawScope.draw(
            density = density,
            layoutDirection = layoutDirection,
            canvas = composeCanvas,
            size = Size(sizePx.toFloat(), sizePx.toFloat())
        ) {
            with(painter) {
                draw(
                    size = Size(sizePx.toFloat(), sizePx.toFloat()),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(tint)
                )
            }
        }
        bitmap
    }
}

private fun createCircularPawDrawable(
    context: Context,
    backgroundColor: Int,
    pawBitmap: Bitmap,
    isSelected: Boolean
): Drawable {
    val density = context.resources.displayMetrics.density
    val sizeDp = if (isSelected) 46 else 40
    val sizePx = (sizeDp * density).toInt()
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val radius = sizePx / 2f

    if (isSelected) {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(radius, radius, radius, borderPaint)
    }

    val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = backgroundColor
        style = Paint.Style.FILL
    }
    val innerRadius = if (isSelected) radius - (3 * density) else radius
    canvas.drawCircle(radius, radius, innerRadius, circlePaint)

    val left = (sizePx - pawBitmap.width) / 2f
    val top = (sizePx - pawBitmap.height) / 2f
    canvas.drawBitmap(pawBitmap, left, top, null)

    return BitmapDrawable(context.resources, bitmap)
}