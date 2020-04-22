package com.eduardo.economise

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    //Variáveis Globais
    private var email: String? = null
    private var password: String? = null

    //UI
    private var tvForgotPassword: TextView? = null
    private var etEmail: TextView? = null
    private var etPassword: TextView? = null
    private var btnLogin: TextView? = null
    lateinit var layout: View
    lateinit var progressBar: ProgressBar
    lateinit var imLogin: ImageView
    lateinit var imBack: ImageView
    lateinit var ti_email: TextInputLayout
    lateinit var ti_password: TextInputLayout

    //BD
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initialise()
    }

    private fun initialise() {
        tvForgotPassword = findViewById(R.id.tv_forgot_password) as TextView
        etEmail = findViewById(R.id.et_email) as EditText
        etPassword = findViewById(R.id.et_password) as EditText
        btnLogin = findViewById(R.id.btn_login) as Button
        imLogin = findViewById(R.id.imLogin)
        imBack = findViewById(R.id.imBack)
        ti_email = findViewById(R.id.ti_email)
        ti_password = findViewById(R.id.ti_password)

        mAuth = FirebaseAuth.getInstance()

        tvForgotPassword!!.setOnClickListener{
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        imLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
        }

        imBack.setOnClickListener { finish() }

        btnLogin!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        progressBar = findViewById(R.id.progressBar)
        layout = findViewById(R.id.layout)

        progressBar.getIndeterminateDrawable().setColorFilter(
            Color.rgb(0,163,81), android.graphics.PorterDuff.Mode.SRC_IN)

        etEmail?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                ti_email.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        etPassword?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                ti_password.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE)

            enableViews(layout, false)

            mAuth!!.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this){ task ->
                progressBar.setVisibility(View.INVISIBLE)

                enableViews(layout, true)

                if (task.isSuccessful) {
                    updateUi()
                } else {
                    Toast.makeText(this@LoginActivity, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            if (TextUtils.isEmpty(email)) {
                ti_email.error = "Preencha com seu e-mail"
            }

            if (TextUtils.isEmpty(password)) {
                ti_password.error = "Preencha sua senha"
            }
        }
    }

    private fun updateUi() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
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
