package com.example.iotclient.serviceProxy

import android.location.Location
import android.util.Log
import com.example.weddingapp.ui.map.GPSPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.*
import okhttp3.Dispatcher
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Calendar

object ServiceProxy {

    private val client = OkHttpClient()
    private const val baseUrl = "http://10.42.0.94:80"

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
            val json = Json.encodeToString(LocationDTO(location.latitude, location.longitude, 0f, ""))
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

    fun setLight(status: Boolean) : Boolean {

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
        return response.code == 200

    }

    fun setLock(status: Boolean) : Boolean {

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
            return response.code == 200




    }

    fun getSensorData(): SensorDTO {
        val request = Request.Builder()
            .url("$baseUrl/sensors")
            .get()
            .build()

        val response = client.newCall(request).execute()

        val body = response.body?.string()
            ?: throw IllegalStateException("Response body was null")

        Log.d("myApp", body)
        response.close()

        return Json.decodeFromString(body)
    }

    fun triggerFindMode() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("$baseUrl/findMode")
                    .get()
                    .build()

                val response = client.newCall(request).execute()
                Log.d("MyApp", "FindMode response: ${response.code}")
                response.close()
            } catch (e: Exception) {
                Log.e("MyApp", "FindMode error", e)
            }
        }
    }

    fun submitSettings(auto_light : Boolean,
                       auto_light_level_on : String,
                       auto_light_level_off: String,
                       proximity_lock : Boolean,
                       proximity_meters : String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val SettingsDTO = SettingsDTO(auto_light, auto_light_level_on.toFloat(), auto_light_level_off.toFloat(), proximity_lock, proximity_meters.toFloat())
                val json = Json.encodeToString(SettingsDTO)
                Log.d("myApp", json)
                val request = Request.Builder()
                    .url(baseUrl+"/settings")
                    .put(json.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()

                response.close()

            } catch (e: Exception){
                Log.e("MyApp", "Submit Error", e)
            }
        }
    }

    fun getHistoryForDate(startDate : Calendar, endDate : Calendar) : List<GPSPoint> {
        try {
            val formatter = SimpleDateFormat("ddMMyy")

            val jsonBody = " { \"start\": \""+ formatter.format(startDate.time).toString() +"\", \"end\": \""+formatter.format(endDate.time).toString() +"\"}"

            // Create request body
            // Log.d("myApp", jsonBody)
            val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())

            // Build PUT request
            val request = Request.Builder()
                .url(baseUrl+"/getGps")
                .put(requestBody)
                .build()

            val response = client.newCall(request).execute()

            if (response.code == 200) {
                val string = response.body?.string() ?: "[]"
                Log.d("myApp", string)
                response.close()
                val locationDTOlist : List<LocationDTO>  = Json.decodeFromString(string);
                return locationDTOlist.map { e -> e.toGPSPoint() }
            } else {
                response.close()
                return emptyList()
            }

        } catch (e : Exception){
            Log.e("MyApp", "Map Error", e)
            return  emptyList()
        }


    }


}
