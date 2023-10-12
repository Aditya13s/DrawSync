package com.aditya.drawsync.data

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager

class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val wiFiDirectActionListener: WiFiDirectActionListener
) : BroadcastReceiver() {

    companion object {

        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        }

    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {

                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val enabled = intent.getIntExtra(
                        WifiP2pManager.EXTRA_WIFI_STATE, -1
                    ) == WifiP2pManager.WIFI_P2P_STATE_ENABLED

                    wiFiDirectActionListener.wifiP2pEnabled(enabled)
                    if (!enabled) {
                        wiFiDirectActionListener.onPeersAvailable(emptyList())
                    }
                }

                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    manager.requestPeers(channel) {
                        wiFiDirectActionListener.onPeersAvailable(
                            it.deviceList
                        )
                    }
                }

                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    val networkInfo =
                        intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
                    if (networkInfo != null && networkInfo.isConnected) {
                        manager.requestConnectionInfo(channel) { info ->
                            if (info != null) {
                                wiFiDirectActionListener.onConnectionInfoAvailable(info)
                            }
                        }
                    } else {
                        wiFiDirectActionListener.onDisconnection()
                    }
                }

                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    val wifiP2pDevice =
                        intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    if (wifiP2pDevice != null) {
                        wiFiDirectActionListener.onSelfDeviceAvailable(wifiP2pDevice)
                    }
                }


            }
        }
    }
}
