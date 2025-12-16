package com.example.iotclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iotclient.ui.theme.IoTClientTheme
import com.example.weddingapp.ui.map.MapScreen
import com.example.weddingapp.ui.settings.SettingsScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.content.Context

class MainActivity : ComponentActivity() {

	private lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel = MainViewModel()
        setContent {
            IoTClientTheme {
                MainScreen(mainViewModel)
            }
        }

	ProximityCheck().start()
    }
}

@Composable
fun MainScreen(viewModel : MainViewModel) {
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(currentScreen, viewModel = viewModel, onNavigate = { screen -> currentScreen = screen })
        "map" -> MapScreen(onNavigate = { screen -> currentScreen = screen })
        "settings" -> SettingsScreen(onNavigate = { screen -> currentScreen = screen })
    }
}

