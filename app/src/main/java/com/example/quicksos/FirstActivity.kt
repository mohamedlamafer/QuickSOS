package com.example.quicksos

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val edtNumber = findViewById<EditText>(R.id.edtNumber)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {

            val number = edtNumber.text.toString()

            if (number.isNotEmpty()) {

                val prefs: SharedPreferences =
                    getSharedPreferences("sos_prefs", MODE_PRIVATE)

                prefs.edit().putString("emergency_number", number).apply()

                startActivity(Intent(this, MainActivity::class.java))
                finish()

            } else {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
