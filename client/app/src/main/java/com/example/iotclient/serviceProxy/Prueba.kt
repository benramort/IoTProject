package com.example.iotclient.serviceProxy

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class Prueba {

    private companion object {
        val client = OkHttpClient()
    }

    fun printSomething() {
        Log.d("MyApp","Hello")
    }

    fun fetch() {
        CoroutineScope(Dispatchers.IO).launch {
            val request = Request.Builder()
                .url("http://10.42.0.55:80")
                .build()

            val response = client.newCall(request).execute()
            try {
                Log.d("MyApp", response.body?.string() ?:"")
            } finally {
                response.close()
            }
        }
    }

}