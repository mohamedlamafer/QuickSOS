package com.example.quicksos

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    // استخدام var بدلاً من val لتمكين التعديل
    var prefs: SharedPreferences? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // تعيين SharedPreferences هنا
        prefs = getSharedPreferences("sos_prefs", MODE_PRIVATE)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        val swReminder = findViewById<SwitchCompat>(R.id.swReminder)
        val swFlash = findViewById<SwitchCompat>(R.id.swFlash)
        val swVibrate = findViewById<SwitchCompat>(R.id.swVibrate)
        val swSound = findViewById<SwitchCompat>(R.id.swSound)

        // تحميل القيم من SharedPreferences
        swReminder.isChecked = prefs?.getBoolean("pref_reminder", false) ?: false
        swFlash.isChecked = prefs?.getBoolean("pref_flash", true) ?: true
        swVibrate.isChecked = prefs?.getBoolean("pref_vibrate", true) ?: true
        swSound.isChecked = prefs?.getBoolean("pref_sound", true) ?: true

        swReminder.setOnCheckedChangeListener { _, isChecked ->
            prefs?.edit()?.putBoolean("pref_reminder", isChecked)?.apply()

            if (isChecked) {
                requestNotifPermissionIfNeeded()
                startReminderWork()
                Toast.makeText(this, "Notifications activées", Toast.LENGTH_SHORT).show()
            } else {
                stopReminderWork()
                Toast.makeText(this, "Notifications désactivées", Toast.LENGTH_SHORT).show()
            }
        }

        swFlash.setOnCheckedChangeListener { _, isChecked ->
            prefs?.edit()?.putBoolean("pref_flash", isChecked)?.apply()
            Toast.makeText(
                this,
                if (isChecked) "Flash activé" else "Flash désactivé",
                Toast.LENGTH_SHORT
            ).show()
        }

        swVibrate.setOnCheckedChangeListener { _, isChecked ->
            prefs?.edit()?.putBoolean("pref_vibrate", isChecked)?.apply()
            Toast.makeText(
                this,
                if (isChecked) "Vibration activée" else "Vibration désactivée",
                Toast.LENGTH_SHORT
            ).show()
        }

        swSound.setOnCheckedChangeListener { _, isChecked ->
            prefs?.edit()?.putBoolean("pref_sound", isChecked)?.apply()
            Toast.makeText(
                this,
                if (isChecked) "Son activé" else "Son désactivé",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startReminderWork() {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(15, TimeUnit.MINUTES).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "sos_reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun stopReminderWork() {
        WorkManager.getInstance(this).cancelUniqueWork("sos_reminder_work")
    }

    private fun requestNotifPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    500
                )
            }
        }
    }
}
