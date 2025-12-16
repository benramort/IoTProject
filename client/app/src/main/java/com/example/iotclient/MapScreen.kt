package com.example.weddingapp.ui.map

import android.app.DatePickerDialog
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iotclient.MapViewModel
import com.example.iotclient.PurpleSoft
import com.example.weddingapp.ui.settings.PurpleMain
import kotlinx.coroutines.launch
import java.util.*

val RoutePurple = Color(0xFF7C4DFF)
val RoutePink = Color(0xFFFF69B4)
val RouteYellow = Color(0xFFFFA500)
val KeyPointColor = Color.Black

data class GPSPoint(
    val lat: Double,
    val lon: Double,
    val speed: Float
)

@Composable
fun MapScreen(onNavigate: (String) -> Unit,
              viewModel: MapViewModel = viewModel()) {
    val context = LocalContext.current
    val currentDay by viewModel.currentDay.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val scope = rememberCoroutineScope()

    // Fetch GPS data for the current day initially


    Scaffold(
        bottomBar = { BottomNavigationBar(currentScreen = "map", onNavigate = onNavigate) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapTopBar(
                currentDay = currentDay,
                onPreviousDay = {
                    viewModel.setDay(
                        (currentDay.clone() as Calendar).apply {
                            add(Calendar.DAY_OF_MONTH, -1)
                        }
                    )},
                onNextDay = {
                    viewModel.setDay(
                        (currentDay.clone() as Calendar).apply {
                            add(Calendar.DAY_OF_MONTH, 1)
                        }
                    )},
                onDateSelected = { y, m, d ->
                    viewModel.setDay(
                        Calendar.getInstance().apply { set(y, m, d) }
                    )
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                FakeMapCanvas(routePoints = routePoints, modifier = Modifier.fillMaxSize())

                RouteLegend(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun FakeMapCanvas(routePoints: List<GPSPoint>, modifier: Modifier = Modifier) {
    if (routePoints.isEmpty()) return

    Canvas(modifier = modifier) {
        drawRect(color = Color(0xFFDBDBDB)) // Background

        val minLat = routePoints.minOf { it.lat }
        val maxLat = routePoints.maxOf { it.lat }
        val minLon = routePoints.minOf { it.lon }
        val maxLon = routePoints.maxOf { it.lon }

        fun mapToCanvas(lat: Double, lon: Double): Offset {
            val x = ((lon - minLon) / (maxLon - minLon).coerceAtLeast(0.0001)) * size.width
            val y = size.height - ((lat - minLat) / (maxLat - minLat).coerceAtLeast(0.0001)) * size.height
            return Offset(x.toFloat(), y.toFloat())
        }

        for (i in 0 until routePoints.size - 1) {
            val start = routePoints[i]
            val end = routePoints[i + 1]

            val color = when {
                start.speed <= 15f -> RoutePurple
                start.speed <= 35f -> RoutePink
                else -> RouteYellow
            }

            drawLine(
                color = color,
                start = mapToCanvas(start.lat, start.lon),
                end = mapToCanvas(end.lat, end.lon),
                strokeWidth = 12f,
                cap = StrokeCap.Round
            )
        }

        routePoints.forEachIndexed { index, point ->
            val offset = mapToCanvas(point.lat, point.lon)
            drawCircle(color = KeyPointColor, radius = 14f, center = offset)

            drawContext.canvas.nativeCanvas.drawText(
                "P${index + 1}",
                offset.x + 20f,
                offset.y - 10f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textSize = 36f
                    isFakeBoldText = true
                }
            )
        }
    }
}

@Composable
fun RouteLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Legend", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            LegendItem(RoutePurple, "Slow section")
            LegendItem(RoutePink, "Medium section")
            LegendItem(RouteYellow, "Fast section")
            LegendItem(KeyPointColor, "Key point")
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(10.dp))
        Text(text)
    }
}

@Composable
fun MapTopBar(
    currentDay: Calendar,
    onPreviousDay: () -> Unit,
    onNextDay: () -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit
) {
    val context = LocalContext.current

    val dayString =
        "${currentDay.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())}, " +
                "${currentDay.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())} " +
                "${currentDay.get(Calendar.DAY_OF_MONTH)}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousDay) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day")
        }

        Text(dayString, style = MaterialTheme.typography.titleMedium)

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                val year = currentDay.get(Calendar.YEAR)
                val month = currentDay.get(Calendar.MONTH)
                val day = currentDay.get(Calendar.DAY_OF_MONTH)

                val dialog = DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        onDateSelected(selectedYear, selectedMonth, selectedDay)
                    },
                    year,
                    month,
                    day
                )

                dialog.setOnShowListener {
                    dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)?.setTextColor(PurpleMain.toArgb())
                    dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)?.setTextColor(PurpleMain.toArgb())
                }

                dialog.show()
            }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Select Day")
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onNextDay) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentScreen: String, onNavigate: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        tonalElevation = 8.dp,
        shadowElevation = 20.dp,
        color = PurpleSoft.copy(alpha = 0.85f),
        shape = RoundedCornerShape(28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigate("map") }) {
                Icon(
                    Icons.Default.Map,
                    contentDescription = "Map",
                    tint = if (currentScreen == "map") PurpleMain else Color(0xFF666666),
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(onClick = { onNavigate("home") }) {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentScreen == "home") PurpleMain else Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = { onNavigate("settings") }) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = if (currentScreen == "settings") PurpleMain else Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}


fun getHistoryForDate(date: Calendar): List<GPSPoint> {
    // Call backend API here; currently stub returns dummy data
    // Replace with your actual get_history API call
    // Example:
    // val json = service.getHistory(date)
    // return json.map { GPSPoint(it.lat, it.lon, it.speed) }

    // Temporary fake data for testing
    return listOf(
        GPSPoint(65.05, 25.48, 10f),
        GPSPoint(65.052, 25.481, 12f),
        GPSPoint(65.053, 25.482, 20f),
        GPSPoint(65.055, 25.485, 15f)
    )
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    MapScreen(onNavigate = {})
}