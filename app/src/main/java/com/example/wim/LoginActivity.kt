package com.example.wim

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sf = getSharedPreferences("time", 0)
        val y = sf.getString("yearMonth", "")
        Toast.makeText(this, y, Toast.LENGTH_SHORT).show()
    }
}