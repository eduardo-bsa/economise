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

class LoginActivity : AppCompatActivity() {

    private var TAG = "LoginActivity"

    //Variáveis Globais

    private var email: String? = null
    private var password: String? = null

    //UI

    private var tvForgotPassword: TextView? = null
    private var etEmail: TextView? = null
    private var etPassword: TextView? = null
    private var btnLogin: TextView? = null
    private var btnCreateAccount: TextView? = null
    lateinit var layout: View
    lateinit var progressBar: ProgressBar

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
        btnCreateAccount = findViewById(R.id.btn_register_account) as Button

        mAuth = FirebaseAuth.getInstance()

        tvForgotPassword!!.setOnClickListener{
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        btnCreateAccount!!.setOnClickListener {
            startActivity(Intent(this@LoginActivity, CreateAccountActivity::class.java))
        }

        btnLogin!!.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        progressBar = findViewById(R.id.progressBar)
        layout = findViewById(R.id.layout)

        progressBar.getIndeterminateDrawable().setColorFilter(
            Color.rgb(0,126,0), android.graphics.PorterDuff.Mode.SRC_IN)

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE)

            enableViews(layout, false)

            Log.d(TAG, "Login do usuário")

            mAuth!!.signInWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this){ task ->
                progressBar.setVisibility(View.INVISIBLE)

                enableViews(layout, true)

                if (task.isSuccessful) {
                    Log.d(TAG, "Logado com sucesso")
                    updateUi()
                } else {
                    Log.e(TAG, "Erro ao logar", task.exception)
                    Toast.makeText(this@LoginActivity, "Usuário ou senha incorretos", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@LoginActivity, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
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
