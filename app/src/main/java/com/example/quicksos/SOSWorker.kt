package com.example.quicksos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

class SOSWorker(
    appContext: android.content.Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {

        val emergencyNumber = inputData.getString("number")
            ?: return Result.failure()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.failure()
        }

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(applicationContext)

        val location = try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).await()
        } catch (e: Exception) {
            null
        }

        if (location == null) {
            return Result.retry()
        }

        val message =
            "SOS! J'ai besoin d'aide. Ma localisation : https://maps.google.com/?q=${location.latitude},${location.longitude}"

        try {
            SmsManager.getDefault().sendTextMessage(
                emergencyNumber,
                null,
                message,
                null,
                null
            )
        } catch (e: Exception) {
            return Result.retry()
        }

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val intent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:$emergencyNumber")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                applicationContext.startActivity(intent)
            } catch (_: Exception) {}
        }

        return Result.success()
    }
}
