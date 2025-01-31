package com.example.abrak.ui.View.activity

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.abrak.R
import com.example.abrak.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.splash) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        binding.appVersion.setText(packageInfo.versionName)

        val anim = ObjectAnimator.ofFloat(binding.logoHolder, "translationY", -1000f, 0f)
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