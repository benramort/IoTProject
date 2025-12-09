package com.example.iotclient.serviceProxy

import android.location.Location
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

object ServiceProxy {

    private val client = OkHttpClient()
    private const val baseUrl = "http://10.42.0.55:80"

    fun fetch() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder()
                .url(baseUrl)
                .build()

            val response = client.newCall(request).execute()
            try {
                Log.d("MyApp", response.body?.string() ?:"")
            } finally {
                response.close()
            }
        }
    }

    fun proximityCheck(location : Location) {
        CoroutineScope(Dispatchers.IO).launch {
            val json = Json.encodeToString(LocationDTO(location.latitude, location.longitude))
            Log.d("myApp", json)
            val requestBody = json.toRequestBody()

            val request = Request.Builder()
                .url(baseUrl+"/proximityCheck")
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()

            Log.d("myApp", response.body.toString())
            response.close()
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