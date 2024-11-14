package com.msbahng.commonutils.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

interface WifiManagerInterface {
    fun getWifiName(scope: CoroutineScope): Flow<String?>
}

class WifiNameManager constructor(
    private val context: Context
) : WifiManagerInterface {

    @SuppressLint("MissingPermission", "ServiceCast")
    override fun getWifiName(scope: CoroutineScope): Flow<String?> = callbackFlow {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val request = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build()

            val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

            val networkCallback = @RequiresApi(Build.VERSION_CODES.S)
            object : ConnectivityManager.NetworkCallback(FLAG_INCLUDE_LOCATION_INFO) {
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    val wifiInfo = networkCapabilities.transportInfo as WifiInfo
                    val ssid = wifiInfo.ssid

                    trySend(ssid)
                }
            }

            connectivityManager.registerNetworkCallback(request, networkCallback)

        } else {

            val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo: WifiInfo = manager.connectionInfo
            val name = wifiInfo.ssid

            trySend(name)
        }

        awaitClose {

        }

    }.shareIn(
        scope,
        replay = 0,
        started = SharingStarted.WhileSubscribed()
    )
}