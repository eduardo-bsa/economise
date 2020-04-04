package com.eduardo.economise

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    //UI
    private var etEmail: EditText? = null
    private var btnSubmit: Button? = null
    lateinit var layout: View
    lateinit var progressBar: ProgressBar

    //BD
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        inicialise()
    }

    private fun inicialise() {
        etEmail = findViewById(R.id.et_email) as EditText
        btnSubmit = findViewById(R.id.btn_submit) as Button

        mAuth = FirebaseAuth.getInstance()

        btnSubmit!!.setOnClickListener { sendPasswordEmail() }

    }

    private fun sendPasswordEmail() {
        val email = etEmail?.text.toString()
        progressBar = findViewById(R.id.progressBar)
        layout = findViewById(R.id.layout)

        progressBar.getIndeterminateDrawable().setColorFilter(
            Color.rgb(0,126,0), android.graphics.PorterDuff.Mode.SRC_IN)

        if (!TextUtils.isEmpty(email)) {
            progressBar.setVisibility(View.VISIBLE)

            enableViews(layout, false)

            mAuth!!
                .sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    progressBar.setVisibility(View.INVISIBLE)

                    enableViews(layout, true)

                    if (task.isSuccessful) {
                        val message = "Email enviado"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        updateUI()
                    } else {
                        Toast.makeText(this,"Email não encontrado", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(this,"Entre com um e-mail válido.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val intent = Intent(this@ForgotPasswordActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun enableViews(v: View, enabled: Boolean) {
        if (v is ViewGroup) {
            val vg = v
            for (i in 0 until vg.childCount) {
                enableViews(vg.getChildAt(i), enabled)
            }
        }
        v.setEnabled(enabled)
    }
}
