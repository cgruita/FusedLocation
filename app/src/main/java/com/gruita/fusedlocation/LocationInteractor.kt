package com.gruita.fusedlocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

/**
 * Using Google Location Services APIs, as they're offering a higher accuracy
 * while being a lighter on the battery than the framework location APIs.
 * Also, using the PRIORITY_BALANCED_POWER_ACCURACY, as it offers accurate location
 * while being optimized for power.
 */
class LocationInteractor(val ctx: Context) {

    private lateinit var locationRequest: LocationRequest
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    companion object {
        var currentLocation: Location? = null
    }


    /**
     * initializes the objects needed to get location updates
     */
    fun initialize() {

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(BuildConfig.INTERVAL)
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY


        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)
        if (ActivityCompat.checkSelfPermission(
                ctx,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Permission not granted, cannot continue")
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                currentLocation = locationResult.lastLocation
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.getMainLooper())

        fusedLocationProviderClient.lastLocation.addOnSuccessListener{ location ->

            location?.let {
                currentLocation = location

            }
        }
    }
}