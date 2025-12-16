package com.example.iotclient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iotclient.serviceProxy.ServiceProxy
import com.example.weddingapp.ui.map.GPSPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class MapViewModel : ViewModel() {

    private val _currentDay = MutableStateFlow(Calendar.getInstance())
    val currentDay = _currentDay.asStateFlow()

    private val _routePoints = MutableStateFlow<List<GPSPoint>>(emptyList())
    val routePoints = _routePoints.asStateFlow()

    init {
        loadHistory()
    }

    fun setDay(calendar: Calendar) {
        _currentDay.value = calendar
        loadHistory()
    }

    private fun loadHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            _routePoints.value = ServiceProxy.getHistoryForDate(_currentDay.value, _currentDay.value)
        }
    }
}
