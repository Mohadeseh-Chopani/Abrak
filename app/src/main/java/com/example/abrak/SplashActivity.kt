package com.example.abrak

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.abrak.R

class SplashActivity : AppCompatActivity() {
    lateinit var logoHolder: LinearLayout
    lateinit var version: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        logoHolder = findViewById(R.id.logoHolder)
        version = findViewById(R.id.app_version)


        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        version.setText(packageInfo.versionName)

        val anim = ObjectAnimator.ofFloat(logoHolder, "translationY", -1000f, 0f)
        anim.setDuration(2000)
        anim.start()


        Handler().postDelayed({
            val i: Intent = Intent(
                this@SplashActivity,
                MainActivity::class.java
            )
            startActivity(i)
            finish()
        }, 3000)
    }
}