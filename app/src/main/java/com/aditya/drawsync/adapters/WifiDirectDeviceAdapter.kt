package com.aditya.drawsync.adapters

import android.net.wifi.p2p.WifiP2pDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aditya.drawsync.R

class WifiDirectDeviceAdapter(private val deviceList: Collection<WifiP2pDevice>) :
    RecyclerView.Adapter<WifiDirectDeviceAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.device_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_wifi_device, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = deviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = deviceList.toList()[position].deviceName
    }
}