package com.gruita.fusedlocation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.gruita.fusedlocation.databinding.ActivityMainBinding

const val TAG = "XWG"
private const val REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE = 100
const val MINUTE_IN_MS =  1000*60L
const val FIFTEEN_MINUTES_IN_MS =  MINUTE_IN_MS * 15
const val WORKER_ID = "locationWorker"

class MainActivity : AppCompatActivity() {


    private lateinit var interactor: LocationInteractor

   private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.idTextView.setOnClickListener {
            if(!isLocationPermissionApproved()) {
                requestLocationPermissions()
            } else {
                initializeLocationInteractor()
                LocationWorker.run()
            }
        }

    }

    /**
     * Creating the interactor object, initialization of the request and provider client
     */
    private fun initializeLocationInteractor() {
        interactor = LocationInteractor(this.applicationContext)
        interactor.initialize()
    }


    /**
     * Check if Location permission has been granted
     */
    private fun isLocationPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    /**
     * Requests the permission
     */
    private fun requestLocationPermissions() {
        val provideRationale = isLocationPermissionApproved()

        if (provideRationale) {
            Snackbar.make(
                findViewById(R.id.activity_main),
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Log.d(TAG, "Request permission")
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }


    /**
     * Reacts to the system permissions dialog,
     * Also displaying a snackbar containing a link to
     * the permission setting for the app in case
     * the permission has been denied
     */

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_LOCATION_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    Log.d(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    initializeLocationInteractor()
                    LocationWorker.run()
                }
                else -> {
                    // Permission denied.
                    Snackbar.make(
                        findViewById(R.id.activity_main),
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }

}