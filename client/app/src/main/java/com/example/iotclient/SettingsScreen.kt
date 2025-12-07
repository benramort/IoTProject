package com.example.weddingapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

// -------------------------------------------------------
// COLORS
// -------------------------------------------------------
val PurpleMain = Color(0xFF7C4DFF)
val PurpleSoft = Color(0xFFEDE7F6)
val BackgroundSoft = Color(0xFFF9F7FC)

// -------------------------------------------------------
// SETTINGS SCREEN
// -------------------------------------------------------
@Composable
fun SettingsScreen(onNavigate: (String) -> Unit) {
    var lightCondition by remember { mutableStateOf("") }
    var unlockDistance by remember { mutableStateOf("") }
    var allowAutoUnlock by remember { mutableStateOf(false) }
    var inEnglish by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = { BottomNavigationBar(currentScreen = "settings", onNavigate = onNavigate) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundSoft)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = if (!inEnglish) "Configuración" else "Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Light condition input
            OutlinedTextField(
                value = lightCondition,
                onValueChange = { lightCondition = it },
                label = { Text(if (!inEnglish) "Condiciones de encendido de luz" else "Light conditions") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Unlock distance input
            OutlinedTextField(
                value = unlockDistance,
                onValueChange = { unlockDistance = it },
                label = { Text(if (!inEnglish) "Distancia de desbloqueo automático" else "Auto-unlock distance") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Allow auto-unlock switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (!inEnglish) "Permitir desbloqueo automático" else "Allow auto-unlock")
                Spacer(modifier = Modifier.width(12.dp))
                Switch(checked = allowAutoUnlock, onCheckedChange = { allowAutoUnlock = it })
            }
            Spacer(modifier = Modifier.height(24.dp))

            // English toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("English")
                Spacer(modifier = Modifier.width(12.dp))
                Switch(checked = inEnglish, onCheckedChange = { inEnglish = it })
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Submit button
            Button(
                onClick = { /* TODO: Handle submit */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (!inEnglish) "Enviar" else "Submit")
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

// -------------------------------------------------------
// PREVIEW
// -------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(onNavigate = {})
}
