package com.aditya.drawsync.utils

import android.content.Context
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SystemServiceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val wifiManager: WifiManager by lazy {
        context.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    val locationManager: LocationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val wifiP2pManager: WifiP2pManager by lazy {
        context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    }
}