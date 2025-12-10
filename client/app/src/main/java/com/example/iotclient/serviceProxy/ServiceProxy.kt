package com.example.iotclient.serviceProxy

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class ServiceProxy {
    private companion object {
        val client = OkHttpClient()
    }

    val baseUrl = "http://10.42.0.55:80"

    fun getTemperature(onResult: (Double?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("$baseUrl/sensors")  // GET all sensor data
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                response.close()

                // Parse JSON and extract temperature
                val json = Json.parseToJsonElement(body ?: "{}").jsonObject
                val temperature = json["temperature"]?.jsonPrimitive?.doubleOrNull

                onResult(temperature)
            } catch (e: Exception) {
                Log.e("MyApp", "Error getting temperature", e)
                onResult(null)
            }
        }
    }

    fun getLightLevel(onResult: (Double?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("$baseUrl/sensors")  // GET all sensor data
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string()
                response.close()

                // Parse JSON and extract light level
                val json = Json.parseToJsonElement(body ?: "{}").jsonObject
                val lightLevel = json["ligth_level"]?.jsonPrimitive?.doubleOrNull

                onResult(lightLevel)
            } catch (e: Exception) {
                Log.e("MyApp", "Error getting light level", e)
                onResult(null)
            }
        }
    }


    fun setLight(status: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val state = State(status)
            val jsonBody = Json.encodeToString(state)

            // Create request body
            Log.d("myApp", jsonBody)
            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

            // Build PUT request
            val request = Request.Builder()
                .url(baseUrl+"/light")
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()
            Log.d("MyApp", "Light response: ${response.code}")
            response.close()
        }

    }

    fun setLock(status: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val state = State(status)
            val jsonBody = Json.encodeToString(state)

            // Create request body
            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

            // Build PUT request
            val request = Request.Builder()
                .url(baseUrl+"/lock")
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()
            Log.d("MyApp", "Lock response: ${response.code}")
            response.close()

    }
} }