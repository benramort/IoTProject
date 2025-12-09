package com.example.iotclient

import android.location.Location
import android.util.Log
import com.example.iotclient.serviceProxy.ServiceProxy
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProximityCheck (val fusedLocationClient : FusedLocationProviderClient){

    fun checkPeriodically() {
        CoroutineScope(Dispatchers.Default).launch {
            while (true) {
                try {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location : Location? ->
                            Log.d("myApp", location.toString())
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                ServiceProxy.proximityCheck(location)
                            }
                        }.addOnFailureListener { result -> Log.e("myApp", result.toString()) }
                } catch (ex: SecurityException) {
                    Log.e("myApp","Location permission not granted")
                }

                delay(1000)
            }
        }

    }

}