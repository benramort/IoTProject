package com.example.iotclient.serviceProxy

import com.example.weddingapp.ui.map.GPSPoint
import kotlinx.serialization.Serializable


@Serializable
data class LocationDTO(val lat : Double, val lon : Double, val speed_kmph : Float, val timestamp : String) {

    fun toGPSPoint() : GPSPoint {
        return GPSPoint(lat, lon, speed_kmph)
    }
}