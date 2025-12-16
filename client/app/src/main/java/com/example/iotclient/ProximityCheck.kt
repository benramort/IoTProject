package com.example.iotclient

import android.content.Context
import android.location.Location
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

import com.example.iotclient.serviceProxy.ServiceProxy

class ProximityCheck() {

    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        scope.launch {
            while (true) {
                try {
                    // Simulate a location
                    val location = Location("mock").apply {
                        latitude = 65.059950
                        longitude = 25.466049
                        accuracy = 5f
                    }

                    Log.d("myApp", "Simulated location: $location")
                    ServiceProxy.proximityCheck(location)

                } catch (ex: Exception) {
                    Log.e("myApp", "Error in proximity loop", ex)
                }

                delay(1000) // 1 second interval
            }
        }
    }
}
