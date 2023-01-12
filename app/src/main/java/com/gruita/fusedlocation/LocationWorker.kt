package com.gruita.fusedlocation


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.work.*


import com.gruita.fusedlocation.BuildConfig.INTERVAL
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

/**
 * The worker class which will get the updated location and display it
 */
class LocationWorker(val ctx: Context, params: WorkerParameters) : Worker(ctx, params) {


    companion object {

        /**
         * creating the worker (using a periodic request)
         */
        fun run(): LiveData<WorkInfo> {
            val work = PeriodicWorkRequestBuilder<LocationWorker>(INTERVAL, TimeUnit.MILLISECONDS)
                .build()
//            TODO I ran out of time, so I used the deprecated version.
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                WORKER_ID,
                ExistingPeriodicWorkPolicy.REPLACE,
                work
            )
            return WorkManager.getInstance().getWorkInfoByIdLiveData(work.id)
        }
    }


    /**
     * Gets the location and displays a toast showing it
     */
    override fun doWork(): Result {
        Log.d(TAG, "-------LocationWork")

        LocationInteractor.currentLocation?.let{
            CoroutineScope(Dispatchers.Main).launch{
                val locationAsString = "Location: ${it.latitude}, ${it?.longitude}"
                Toast.makeText(ctx, locationAsString, Toast.LENGTH_LONG).show()

//                TODO. Persisting the location
//                1. SharedPrefs
//                2. Database (room)
            }
        }

        // per Android documentation
        return if(INTERVAL < FIFTEEN_MINUTES_IN_MS) {
            Result.retry()
        } else {
            Result.success()
        }
    }
}