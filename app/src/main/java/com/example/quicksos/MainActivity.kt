package com.example.quicksos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.hardware.camera2.CameraManager
import android.media.MediaPlayer
import android.os.*
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import pub.devrel.easypermissions.EasyPermissions
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var cameraManager: CameraManager
    private var cameraId: String? = null

    private val handler = Handler(Looper.getMainLooper())
    public var flashOn = false
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var settingsClient: SettingsClient
    private lateinit var locationRequest: LocationRequest

    private val prefs by lazy {
        getSharedPreferences("sos_prefs", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val periodicRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sos_reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            periodicRequest
        )

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList.firstOrNull()

        settingsClient = LocationServices.getSettingsClient(this)
        locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 1000
        ).build()

        val imgSOS = findViewById<ImageView>(R.id.imgSOS)
        val btnStopFlash = findViewById<Button>(R.id.btnStopFlash)

        val btnSettings = findViewById<ImageView>(R.id.iconSettings)
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        imgSOS.setOnClickListener {

            imgSOS.animate()
                .scaleX(0.85f)
                .scaleY(0.85f)
                .setDuration(100)
                .withEndAction {

                    imgSOS.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction {

                            val number = prefs.getString("emergency_number", null)
                            if (number.isNullOrEmpty()) {
                                Toast.makeText(
                                    this,
                                    "Aucun numéro d'urgence n'est enregistré.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@withEndAction
                            }

                            if (!EasyPermissions.hasPermissions(
                                    this,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.CAMERA
                                )
                            ) {
                                EasyPermissions.requestPermissions(
                                    this,
                                    "Permissions required for SOS",
                                    100,
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.SEND_SMS,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.CAMERA
                                )
                                return@withEndAction
                            }

                            val enableFlash = prefs.getBoolean("pref_flash", true)
                            val enableVibrate = prefs.getBoolean("pref_vibrate", true)
                            val enableSound = prefs.getBoolean("pref_sound", true)

                            val vibrator = if (enableVibrate) {
                                getSystemService(VIBRATOR_SERVICE) as Vibrator
                            } else null

                            checkLocationEnabled(number, vibrator)

                            var alertRunning = false

                            if (enableFlash) {
                                startFlashLoop()
                                alertRunning = true
                            }

                            if (enableSound) {
                                startSound()
                                alertRunning = true
                            }

                            btnStopFlash.visibility = if (alertRunning) View.VISIBLE else View.GONE


                            if (enableSound) {
                                startSound()
                            } else {
                                stopSound()
                            }
                        }
                        .start()
                }
                .start()
        }

        btnStopFlash.setOnClickListener {
            stopFlashLoop()
            btnStopFlash.visibility = View.GONE
        }
    }

    public fun startFlashLoop() {
        flashOn = true
        handler.post(object : Runnable {
            override fun run() {
                if (!flashOn) return
                toggleFlash(true)
                handler.postDelayed({ toggleFlash(false) }, 300)
                handler.postDelayed(this, 600)
            }
        })
    }

    public fun stopFlashLoop() {
        flashOn = false
        toggleFlash(false)
        handler.removeCallbacksAndMessages(null)
        stopSound()
    }

    private fun toggleFlash(state: Boolean) {
        try {
            cameraId?.let { cameraManager.setTorchMode(it, state) }
        } catch (_: Exception) {}
    }

    private fun checkLocationEnabled(number: String, vibrator: Vibrator?) {

        val request = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)
            .build()

        settingsClient.checkLocationSettings(request)
            .addOnSuccessListener {
                startSOS(number, vibrator)
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(this, 200)
                    } catch (_: IntentSender.SendIntentException) {}
                } else {

                    startSOS(number, vibrator)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200) {
            val number = prefs.getString("emergency_number", null)
            if (!number.isNullOrEmpty()) {
                val enableVibrate = prefs.getBoolean("pref_vibrate", true)
                val vibrator = if (enableVibrate) getSystemService(VIBRATOR_SERVICE) as Vibrator else null
                startSOS(number, vibrator)
            }
        }
    }

    private fun startSound() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm26718)
            mediaPlayer?.isLooping = true
            mediaPlayer?.start()
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    private fun stopSound() {
        try {
            mediaPlayer?.stop()
        } catch (_: Exception) {}
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun startSOS(number: String, vibrator: Vibrator?) {

        val data = Data.Builder()
            .putString("number", number)
            .build()

        val request = OneTimeWorkRequestBuilder<SOSWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(this).enqueue(request)


        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    VibrationEffect.createWaveform(
                        longArrayOf(0, 300, 150, 300),
                        -1
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(longArrayOf(0, 300, 150, 300), -1)
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}
}
