package com.aditya.drawsync

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aditya.drawsync.databinding.ActivityConnectBinding

class ConnectActivity : AppCompatActivity() {
    private lateinit var binding: ActivityConnectBinding
    private val wifiP2pDeviceList = mutableListOf<WifiP2pDevice>()
    private var wifiP2pInfo: WifiP2pInfo? = null

    private var wifiP2pEnabled = false
    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null

    private val wiFiDirectActionListener = object : WiFiDirectActionListener {
        override fun wifiP2pEnabled(enabled: Boolean) {
            wifiP2pEnabled = enabled
        }

        override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo) {

        }

        override fun onDisconnection() {
        }

        override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice) {
        }

        override fun onPeersAvailable(wifiP2pDeviceList: Collection<WifiP2pDevice>) {
            Toast.makeText(
                this@ConnectActivity, wifiP2pDeviceList.toList().toString(), Toast.LENGTH_SHORT
            ).show();

        }

        override fun onChannelDisconnected() {
            Toast.makeText(this@ConnectActivity, "channel disconnect", Toast.LENGTH_SHORT).show();
        }

    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)




        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager?.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver = manager?.let {
                WiFiDirectBroadcastReceiver(
                    it, channel, this, wiFiDirectActionListener
                )
            }
        }
        binding.search.setOnClickListener {
            if (!wifiP2pEnabled) {
                Toast.makeText(this@ConnectActivity, "P2P not enabled", Toast.LENGTH_SHORT)
                    .show();
                return@setOnClickListener
            }

            wifiP2pDeviceList.clear()

            manager!!.discoverPeers(channel,
                object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@ConnectActivity,
                            "Discover Success",
                            Toast.LENGTH_SHORT
                        ).show();

                    }

                    override fun onFailure(reasonCode: Int) {
                        Toast.makeText(
                            this@ConnectActivity,
                            "Discover Failed",
                            Toast.LENGTH_SHORT
                        ).show();
                    }
                })
        }

    }


    override fun onResume() {
        super.onResume()
        receiver = WiFiDirectBroadcastReceiver(manager!!, channel!!, this, wiFiDirectActionListener)
        registerReceiver(receiver, WiFiDirectBroadcastReceiver.intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }


}
