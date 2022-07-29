package com.example.socialx.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.example.socialx.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        auth = Firebase.auth
        if(auth==null)
        {
            Toast.makeText(applicationContext,getString(R.string.some_error_occurred),Toast.LENGTH_SHORT).show()
            finish()
        }
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            if(auth.currentUser!=null)
                startActivity(Intent(this,HomeActivity::class.java))
            else
                startActivity(Intent(this,MainActivity::class.java))
            finish()
        },1500)
    }
}