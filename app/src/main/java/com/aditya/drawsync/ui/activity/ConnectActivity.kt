package com.aditya.drawsync.ui.activity

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.aditya.drawsync.adapters.WifiDirectDeviceAdapter
import com.aditya.drawsync.data.WiFiDirectActionListener
import com.aditya.drawsync.data.WiFiDirectBroadcastReceiver
import com.aditya.drawsync.databinding.ActivityConnectBinding
import com.aditya.drawsync.utils.SystemServiceManager
import com.aditya.drawsync.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ConnectActivity : AppCompatActivity(), WiFiDirectActionListener {
    private lateinit var binding: ActivityConnectBinding
    private val wifiP2pDeviceList = mutableListOf<WifiP2pDevice>()

    private var wifiP2pEnabled = false
    private var manager: WifiP2pManager? = null
    private var channel: WifiP2pManager.Channel? = null
    private var receiver: BroadcastReceiver? = null

    private lateinit var wifiDirectDeviceAdapter: WifiDirectDeviceAdapter

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var systemServiceManager: SystemServiceManager


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wifiDirectDeviceAdapter = WifiDirectDeviceAdapter(wifiP2pDeviceList)
        binding.searchedDeviceRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchedDeviceRecyclerView.adapter = wifiDirectDeviceAdapter



        manager = systemServiceManager.wifiP2pManager
        channel = manager?.initialize(this, mainLooper, null)
        channel?.also { channel ->
            receiver = manager?.let {
                WiFiDirectBroadcastReceiver(
                    it, channel, this
                )
            }
        }


        binding.search.setOnClickListener {
            if (!wifiP2pEnabled) {
                utils.showToast("P2P not enabled")
                return@setOnClickListener
            }

            wifiP2pDeviceList.clear()

            manager!!.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    utils.showToast("Discover Success")
                }

                override fun onFailure(reasonCode: Int) {
                    utils.showToast("Discover Failed")
                }
            })
        }

    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        receiver = WiFiDirectBroadcastReceiver(manager!!, channel!!, this)
        registerReceiver(receiver, WiFiDirectBroadcastReceiver.intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun wifiP2pEnabled(enabled: Boolean) {
        wifiP2pEnabled = enabled
    }

    override fun onConnectionInfoAvailable(wifiP2pInfo: WifiP2pInfo) {}

    override fun onDisconnection() {}

    override fun onSelfDeviceAvailable(wifiP2pDevice: WifiP2pDevice) {}

    @SuppressLint("NotifyDataSetChanged")
    override fun onPeersAvailable(wifiP2pDeviceList: Collection<WifiP2pDevice>) {

        this.wifiP2pDeviceList.clear()
        this.wifiP2pDeviceList.addAll(wifiP2pDeviceList)
        this.wifiDirectDeviceAdapter.notifyDataSetChanged()
    }

    override fun onChannelDisconnected() {
        utils.showToast("Channel Disconnected")
    }


}
