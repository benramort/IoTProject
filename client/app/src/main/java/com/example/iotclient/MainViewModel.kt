package com.example.iotclient

import androidx.lifecycle.ViewModel
import com.example.iotclient.serviceProxy.ServiceProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private val _lightState = MutableStateFlow<Boolean?>(null)
    val lightState = _lightState.asStateFlow()

    private val _lockState = MutableStateFlow<Boolean?>(null)
    val lockState = _lockState.asStateFlow()

    private val _temperature = MutableStateFlow<Float?>(null)
    val temperature = _temperature.asStateFlow();

    private val _ligthLevel = MutableStateFlow<Float?>(null)
    val ligthLevel = _ligthLevel.asStateFlow();

    val viewModelScope = CoroutineScope(Dispatchers.IO)

    init {
        updateData()
    }

    fun updateData() {
        viewModelScope.launch {
            val sensorDTO = ServiceProxy.getSensorData()
            _lightState.value = sensorDTO.light_state
            _lockState.value = sensorDTO.lock_state
            _ligthLevel.value = sensorDTO.light_level
            _temperature.value = sensorDTO.temperature
        }

    }

    fun toggleLight() {
        viewModelScope.launch {
            val current = _lightState.value ?: false
            val newValue = !current

            val success = ServiceProxy.setLight(newValue)
            if (success) {
                _lightState.value = newValue
            }
        }
    }

    fun toggleLock() {
        viewModelScope.launch() {
            val current = _lockState.value ?: false
            val newValue = !current

            val success = ServiceProxy.setLock(newValue)
            if (success) {
                _lockState.value = newValue
            }
        }
    }

}