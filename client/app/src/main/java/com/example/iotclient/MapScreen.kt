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
import com.example.iotclient.PurpleSoft
import com.example.weddingapp.ui.settings.PurpleMain
import java.util.*

val PurpleSoft = Color(0xFFEDE7F6)
val PurpleMain = Color(0xFF7C4DFF)
val RoutePurple = Color(0xFF7C4DFF)
val RoutePink = Color(0xFFFF69B4)
val RouteYellow = Color(0xFFFFA500)
val KeyPointColor = Color.Black

@Composable
fun MapScreen(onNavigate: (String) -> Unit) {
    val context = LocalContext.current
    var currentDay by remember { mutableStateOf(Calendar.getInstance()) }

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
                    currentDay = (currentDay.clone() as Calendar).apply {
                        add(Calendar.DAY_OF_MONTH, -1)
                    }
                },
                onNextDay = {
                    currentDay = (currentDay.clone() as Calendar).apply {
                        add(Calendar.DAY_OF_MONTH, 1)
                    }
                },
                onSelectDay = {
                    val year = currentDay.get(Calendar.YEAR)
                    val month = currentDay.get(Calendar.MONTH)
                    val day = currentDay.get(Calendar.DAY_OF_MONTH)

                    val dialog = DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            currentDay = Calendar.getInstance().apply {
                                set(y, m, d)
                            }
                        },
                        year, month, day
                    )

                    // Keep buttons purple
                    dialog.setOnShowListener {
                        dialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                            ?.setTextColor(PurpleMain.toArgb())
                        dialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                            ?.setTextColor(PurpleMain.toArgb())
                    }

                    dialog.show()
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                FakeMapCanvas(modifier = Modifier.fillMaxSize())

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
fun FakeMapCanvas(modifier: Modifier = Modifier) {
    val routePoints = remember {
        listOf(
            Triple(0.1f to 0.2f, 10f, "Start"),
            Triple(0.2f to 0.3f, 20f, "Point 1"),
            Triple(0.3f to 0.25f, 30f, "Point 2"),
            Triple(0.5f to 0.4f, 15f, "End"),
            Triple(0.7f to 0.6f, 40f, "Point 3"),
            Triple(0.85f to 0.4f, 50f, "Point 4")
        )
    }

    Canvas(modifier = modifier) {
        drawRect(color = Color(0xFFDBDBDB))

        // Draw route lines
        for (i in 0 until routePoints.size - 1) {
            val (start, speed, _) = routePoints[i]
            val (end, _, _) = routePoints[i + 1]

            val color = when {
                speed <= 15f -> RoutePurple
                speed <= 35f -> RoutePink
                else -> RouteYellow
            }

            drawLine(
                color = color,
                start = Offset(start.first * size.width, start.second * size.height),
                end = Offset(end.first * size.width, end.second * size.height),
                strokeWidth = 12f,
                cap = StrokeCap.Round
            )
        }

        // Draw key points
        routePoints.forEach { (pos, _, label) ->
            val px = pos.first * size.width
            val py = pos.second * size.height
            drawCircle(color = KeyPointColor, radius = 14f, center = Offset(px, py))

            drawContext.canvas.nativeCanvas.drawText(
                label,
                px + 20f,
                py - 10f,
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
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
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
    onSelectDay: () -> Unit
) {
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
            IconButton(onClick = onSelectDay) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Select Day")
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onNextDay) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
            }
        }
    }
}



// -------------------------------------------------------
// BOTTOM NAVIGATION BAR
// -------------------------------------------------------
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
                Icon(Icons.Default.Map,
                    contentDescription = "Map",
                    tint = if (currentScreen == "map") PurpleMain else Color(0xFF666666),
                    modifier = Modifier.size(30.dp))
            }
            IconButton(onClick = { onNavigate("home") }) {
                Icon(Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp))
            }
            IconButton(onClick = { onNavigate("settings") }) {
                Icon(Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    MapScreen(onNavigate = {})
}
