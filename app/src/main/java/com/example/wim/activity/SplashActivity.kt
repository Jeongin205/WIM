package com.example.wim.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.wim.R
import java.text.SimpleDateFormat
import java.util.*

class SplashActivity : AppCompatActivity() {
    val splashTime: Long = 2000
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val time = System.currentTimeMillis()
        val yearMonth = SimpleDateFormat("yyMM").format(Date(time))
        val month = SimpleDateFormat("MM").format(Date(time))
        val day = SimpleDateFormat("dd").format(Date(time))

        val sharedPreference = getSharedPreferences("time", MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("yearMonth", yearMonth)
        editor.putString("month", month)
        editor.putString("day", day)
        editor.commit()

        Handler().postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, splashTime)
    }
}