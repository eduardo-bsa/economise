package com.eduardo.economise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SplashScreen : AppCompatActivity() {

    //BD

    var firebaseAuth: FirebaseAuth? = null
    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        changeToLogin()
    }

    fun changeToLogin() {

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth!!.getCurrentUser()

        val intent: Intent

        if (firebaseUser != null) {
            intent = Intent(this, MainActivity::class.java)
        } else {
            intent = Intent(this, LoginActivity::class.java)
        }

        Handler().postDelayed({
            intent.change()
        }, 2000)
    }

    fun Intent.change(){
        startActivity(this)
        finish()
    }

}
