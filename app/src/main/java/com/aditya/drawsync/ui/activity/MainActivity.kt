package com.aditya.drawsync.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat
import com.aditya.drawsync.databinding.ActivityMainBinding
import com.aditya.drawsync.utils.SystemServiceManager
import com.aditya.drawsync.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var wifi: WifiManager
    private lateinit var locationManager: LocationManager

    @Inject
    lateinit var utils: Utils

    @Inject
    lateinit var systemServiceManager: SystemServiceManager


    private val requestPermissionLaunch = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { it ->
        if (it.all { it.value }) {
            utils.showToast("All required permissions have been successfully granted.")
        } else {
            onPermissionDenied()
        }
    }

    private fun onPermissionDenied() {
        utils.showToast("Please grant the necessary permissions to continue.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        wifi = systemServiceManager.wifiManager
        locationManager = systemServiceManager.locationManager

        binding.connect.setOnClickListener {
            if (allPermissionGranted()) {
                if (wifiAndLocationIsTurnedOn()) nextActivity(ConnectActivity::class.java)

            } else {
                requestPermissionLaunch.launch(utils.requestedPermissions)
            }

        }

        binding.receive.setOnClickListener {
            if (allPermissionGranted()) {
                if (wifiAndLocationIsTurnedOn()) nextActivity(ReceiveActivity::class.java)

            } else {
                requestPermissionLaunch.launch(utils.requestedPermissions)
            }

        }
    }

    private fun wifiAndLocationIsTurnedOn(): Boolean {
        if (!wifi.isWifiEnabled) {
            Utils(this).showToast("Please enable WiFi to continue.");
            utils.openPanel(this, Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            return false
        }

        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            Utils(this).showToast("Please enable location services to proceed.")
            utils.openPanel(this, Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            return false
        }
        return true

    }


    private fun nextActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        this.startActivity(intent)
    }

    private fun allPermissionGranted(): Boolean {
        utils.requestedPermissions.forEach {
            if (ActivityCompat.checkSelfPermission(
                    this, it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
}