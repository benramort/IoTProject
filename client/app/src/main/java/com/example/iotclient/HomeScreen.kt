package com.example.iotclient

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.iotclient.serviceProxy.ServiceProxy
import com.example.weddingapp.ui.settings.BottomNavigationBar
import com.example.weddingapp.ui.settings.PurpleMain

// -------------------------------------------------------
// COLORS
// -------------------------------------------------------
val PurpleMain = Color(0xFF7C4DFF)
val PurpleSoft = Color(0xFFEDE7F6)
val BackgroundSoft = Color(0xFFF9F7FC)

// -------------------------------------------------------
// MAIN SCREEN
// -------------------------------------------------------
@Composable
fun HomeScreen(
    currentScreen: String,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(currentScreen, onNavigate) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        listOf(BackgroundSoft, Color.White)
                    )
                )
        ) {
            // Top half
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EnvironmentInfoSection()
            }

            // Divider in middle
            Divider(
                color = Color.Gray.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .align(Alignment.CenterHorizontally)
            )

            // Bottom half
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                ControlsSection()
            }
        }
    }
}

// -------------------------------------------------------
// ENVIRONMENT INFO
// -------------------------------------------------------
@Composable
fun EnvironmentInfoSection(serviceProxy: ServiceProxy = ServiceProxy(), modifier: Modifier = Modifier) {

    // State variables for real sensor values
    val temperatureState = remember { mutableStateOf("--") }
    val lightLevelState = remember { mutableStateOf("--") }

    // Load values from the service proxy
    LaunchedEffect(Unit) {
        while(true) {
            serviceProxy.getTemperature { temp ->
                temp?.let { temperatureState.value = "%.1f°".format(it) }
            }
            serviceProxy.getLightLevel { level ->
                level?.let { lightLevelState.value = "%.0f".format(it) }
            }
            kotlinx.coroutines.delay(5000L) // refresh every 5 seconds
        }
    }


    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InfoCard(
            icon = Icons.Default.Thermostat,
            label = "Temperature",
            value = temperatureState.value,
            modifier = Modifier.weight(1f)
        )

        InfoCard(
            icon = Icons.Default.WbSunny,
            label = "Light Level",
            value = lightLevelState.value,
            modifier = Modifier.weight(1f)
        )
    }
}


// -------------------------------------------------------
// INFO CARD
// -------------------------------------------------------
@Composable
fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(50.dp),
                tint = PurpleMain
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// -------------------------------------------------------
// CONTROLS
// -------------------------------------------------------
@Composable
fun ControlsSection(modifier: Modifier = Modifier) {
    val lightOn = remember { mutableStateOf(false) }
    val unlocked = remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // LIGHT BUTTON
        ControlButton(
            icon = Icons.Default.Lightbulb,
            label = "Light",
            iconTint = if (lightOn.value) Color.Yellow else PurpleMain,
            modifier = Modifier.weight(1f),
            onClick = {
                // Call ServiceProxy and set lightOn to true on success
                ServiceProxy().setLight(true) // async
                lightOn.value = true
            }
        )

        // LOCK BUTTON
        ControlButton(
            icon = if (unlocked.value) Icons.Default.LockOpen else Icons.Default.Lock,
            label = "Lock",
            iconTint = if (unlocked.value) Color.Green else PurpleMain,
            modifier = Modifier.weight(1f),
            onClick = {
                // Call ServiceProxy and set unlocked to true on success
                ServiceProxy().setLock(true) // async
                unlocked.value = true
            }
        )
    }
}

// -------------------------------------------------------
// CONTROL BUTTON CARD
// -------------------------------------------------------
@Composable
fun ControlButton(
    icon: ImageVector,
    label: String,
    iconTint: Color = PurpleMain,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(70.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


// -------------------------------------------------------
// BOTTOM NAVIGATION BAR
// -------------------------------------------------------
@Composable
fun BottomNavigationBar(currentScreen: String,onNavigate: (String) -> Unit) {
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
                    tint = Color(0xFF666666),
                    modifier = Modifier.size(30.dp))
            }
            IconButton(onClick = { onNavigate("home") }) {
                Icon(Icons.Default.Home,
                    contentDescription = "Home",
                    tint = if (currentScreen == "home") PurpleMain else Color.Black,
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

// -------------------------------------------------------
// PREVIEW
// -------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(currentScreen = "home", onNavigate = {})
    }
}
