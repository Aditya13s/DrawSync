package com.aditya.drawsync.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.aditya.drawsync.data.WiFiDirectActionListener
import com.aditya.drawsync.data.WiFiDirectBroadcastReceiver
import com.aditya.drawsync.databinding.ActivityReceiveBinding
import com.aditya.drawsync.utils.SystemServiceManager
import com.aditya.drawsync.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import javax.inject.Inject

@AndroidEntryPoint
class ReceiveActivity : AppCompatActivity(), WiFiDirectActionListener {
    private lateinit var binding: ActivityReceiveBinding


    private var wifiP2pEnabled = false
    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null

    private var serverSocket: ServerSocket? = null

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var systemServiceManager: SystemServiceManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReceiveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        manager = systemServiceManager.wifiP2pManager
        channel = manager?.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver = manager?.let {
                WiFiDirectBroadcastReceiver(
                    it, channel, this
                )
            }
        }


    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag", "MissingPermission")
    override fun onResume() {
        super.onResume()
        receiver = WiFiDirectBroadcastReceiver(manager!!, channel!!, this)
        registerReceiver(receiver, WiFiDirectBroadcastReceiver.intentFilter)
    }

    @SuppressLint("MissingPermission")
    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)

    }

    override fun wifiP2pEnabled(enabled: Boolean) {
        wifiP2pEnabled = enabled
    }

    override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo) {
        if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
            // This device is the group owner (server). Set up a server socket.
            try {
                serverSocket = ServerSocket(8988)
                val serverThread = ServerThread(serverSocket)
                serverThread.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onDisconnection() {}

    override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice) {}

    override fun onPeersAvailable(wifiP2pDeviceList: Collection<WifiP2pDevice>) {}

    override fun onChannelDisconnected() {
        utils.showToast("Channel Disconnected")
    }

    private inner class ServerThread(private val serverSocket: ServerSocket?) : Thread() {
        override fun run() {
            try {
                val clientSocket: Socket = serverSocket?.accept() ?: return
                // Handle the incoming connection and data here
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}