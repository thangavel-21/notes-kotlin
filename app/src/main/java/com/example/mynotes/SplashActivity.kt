package com.example.mynotes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splashscreen)

        mAuth = FirebaseAuth.getInstance();
        val user = mAuth.currentUser;

        Handler().postDelayed({
            if (user != null) {
                val Intent = Intent(this, NotesActivity::class.java)
                startActivity(Intent);
                finish();
            } else {
                val Intent = Intent(this, LoginActivity::class.java)
                startActivity(Intent);
                finish();
            }
        }, 2000)
        FirebaseApp.initializeApp(this)

    }
}