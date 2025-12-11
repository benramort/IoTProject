package com.example.iotclient.serviceProxy

import kotlinx.serialization.Serializable

@Serializable
data class SensorDTO (val timestamp : Long,
                      val light_level : Float,
                      val temperature : Float,
                      /*val location : LocationDTO,*/
                      val lock_state : Boolean,
                      val light_state : Boolean) {
}