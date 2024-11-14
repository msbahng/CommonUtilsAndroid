package com.msbahng.commonutils.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

enum class LocationErrorCode {
    denied,
    failed;
}

class LocationError(
    val code: LocationErrorCode,
    message: String?
) : Exception(message)


interface LocationManagerInterface {

    fun locationFlow(scope: CoroutineScope): Flow<Location?>
    fun currentLocation(scope: CoroutineScope): Flow<Location?>
}

class LocationManager constructor(
    private val context: Context
) : LocationManagerInterface {

    private val TAG = "LocationManager"

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    override fun locationFlow(scope: CoroutineScope): Flow<Location?> = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result ?: return
                Log.d(TAG, "New location: ${result.lastLocation.toString()}")
                // Send the new location to the Flow observers
                trySend(result.lastLocation)
            }
        }

        Log.d(TAG, "Starting location updates")

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(1000)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            close(e) // in case of exception, close the Flow
        }

        awaitClose {

        }
    }.shareIn(
        scope,
        replay = 0,
        started = SharingStarted.WhileSubscribed()
    )

    @SuppressLint("MissingPermission")
    override fun currentLocation(scope: CoroutineScope): Flow<Location?> = callbackFlow {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { location ->

                if (location == null) {
//                    close(LocationError(LocationErrorCode.failed, "locatoin is null"))
                    trySend(null)       // temporary
                } else {
                    Log.d(TAG, "current location: ${location.toString()}")
                    trySend(location)
                }
            }
            .addOnFailureListener {
//                close(LocationError(LocationErrorCode.failed, it.message ?: ""))
                trySend(null)       // temporary
            }
            .addOnCanceledListener {
                close(LocationError(LocationErrorCode.failed, "getCurrentLocation canceled"))
            }

        awaitClose {

        }

    }.shareIn(
        scope,
        replay = 0,
        started = SharingStarted.WhileSubscribed()
    )
}
