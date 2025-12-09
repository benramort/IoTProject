package com.example.weddingapp.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val PurpleMain = Color(0xFF7C4DFF)
val PurpleSoft = Color(0xFFEDE7F6)
val BackgroundSoft = Color(0xFFF9F7FC)

@Composable
fun SettingsScreen(onNavigate: (String) -> Unit) {

    var autoLight by remember { mutableStateOf(false) }
    var lightTemperature by remember { mutableStateOf("20°C") }
    val lightOptions = listOf("15°C", "20°C", "25°C", "30°C")

    var autoUnlock by remember { mutableStateOf(false) }
    var unlockDistance by remember { mutableStateOf("1 m") }
    val distanceOptions = listOf("0.5 m", "1 m", "2 m", "5 m")

    var selectedLanguage by remember { mutableStateOf("English") }
    val languageOptions = listOf("English", "Español")

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
                text = if (selectedLanguage == "Español") "Configuración" else "Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // AUTO LIGHT
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedLanguage == "Español") "Encendido automático de luz" else "Enable auto light")
                Spacer(modifier = Modifier.width(12.dp))
                Switch(checked = autoLight, onCheckedChange = { autoLight = it })
            }

            if (autoLight) {
                Spacer(modifier = Modifier.height(8.dp))
                SimpleDropdown(
                    options = lightOptions,
                    selectedOption = lightTemperature,
                    onOptionSelected = { lightTemperature = it },
                    label = if (selectedLanguage == "Español") "Temperatura de activación" else "Activation temperature"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AUTO UNLOCK
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedLanguage == "Español") "Desbloqueo automático" else "Enable auto-unlock")
                Spacer(modifier = Modifier.width(12.dp))
                Switch(checked = autoUnlock, onCheckedChange = { autoUnlock = it })
            }

            if (autoUnlock) {
                Spacer(modifier = Modifier.height(8.dp))
                SimpleDropdown(
                    options = distanceOptions,
                    selectedOption = unlockDistance,
                    onOptionSelected = { unlockDistance = it },
                    label = if (selectedLanguage == "Español") "Distancia de desbloqueo" else "Unlock distance"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LANGUAGE
            SimpleDropdown(
                options = languageOptions,
                selectedOption = selectedLanguage,
                onOptionSelected = { selectedLanguage = it },
                label = if (selectedLanguage == "Español") "Idioma" else "Language"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SUBMIT BUTTON
            Button(
                onClick = {
                    println("----- SETTINGS SUBMITTED -----")
                    println("Auto Light: $autoLight")
                    println("Light Temperature: $lightTemperature")
                    println("Auto Unlock: $autoUnlock")
                    println("Unlock Distance: $unlockDistance")
                    println("Language: $selectedLanguage")
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PurpleMain,
                    contentColor = Color.White
                )
            ) {
                Text(if (selectedLanguage == "Español") "Enviar" else "Submit")
            }
        }
    }
}

@Composable
fun SimpleDropdown(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    label: String
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontWeight = FontWeight.Medium)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
        ) {
            // Clickable box that triggers dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .clickable {
                        expanded = !expanded
                        println("Dropdown clicked! Expanded: $expanded") // Debug
                    }
                    .padding(horizontal = 12.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedOption, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }

            // DropdownMenu - removed fillMaxWidth()
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    println("Dropdown dismissed") // Debug
                }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            println("Selected: $option") // Debug
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
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

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(onNavigate = {})
}
