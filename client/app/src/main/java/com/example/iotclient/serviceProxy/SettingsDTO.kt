package com.example.iotclient.serviceProxy

import kotlinx.serialization.Serializable

@Serializable
data class SettingsDTO (val auto_light : Boolean,
                        val auto_light_level_on : Float,
                        val auto_light_level_off: Float,
                        val proximity_lock : Boolean,
                        val proximity_meters : Float) {}