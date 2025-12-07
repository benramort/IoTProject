package com.example.iotclient.serviceProxy

import kotlinx.serialization.Serializable

@Serializable
data class State(val state: Boolean) {
}