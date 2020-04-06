package com.eduardo.economise

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class MenuActivity : AppCompatActivity() {

    lateinit var btEmail: Button
    lateinit var tvLogin: TextView
    lateinit var imLogin: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initialise()
    }

    private fun initialise() {
        btEmail = findViewById(R.id.btEmail)
        tvLogin = findViewById(R.id.tvLogin)
        imLogin = findViewById(R.id.imLogin)

        btEmail.setOnClickListener {
            val intent = Intent(this@MenuActivity, CreateAccountActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        tvLogin.setOnClickListener {
            val intent = Intent(this@MenuActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        imLogin.setOnClickListener {
            val intent = Intent(this@MenuActivity, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }
    }
}
