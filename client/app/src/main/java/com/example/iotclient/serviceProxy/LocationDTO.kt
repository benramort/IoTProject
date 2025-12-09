package com.example.iotclient.serviceProxy

import kotlinx.serialization.Serializable


@Serializable
data class LocationDTO(val lat : Double, val lon : Double)