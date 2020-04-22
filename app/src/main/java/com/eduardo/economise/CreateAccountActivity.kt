package com.eduardo.economise

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateAccountActivity : AppCompatActivity() {

    //UI
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    lateinit var layout: View
    lateinit var progressBar: ProgressBar
    lateinit var imBack: ImageView
    lateinit var tiEmail: TextInputLayout
    lateinit var tiSenha: TextInputLayout

    //BD
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    //Variáveis globais
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initialise()
    }

    private fun initialise() {
        etEmail = findViewById(R.id.et_email) as EditText
        etPassword = findViewById(R.id.et_password) as EditText
        btnCreateAccount = findViewById(R.id.btn_register) as Button
        imBack = findViewById(R.id.imBack)
        tiEmail = findViewById(R.id.tiEmail)
        tiSenha = findViewById(R.id.tiSenha)

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("users")
        mAuth = FirebaseAuth.getInstance()

        imBack.setOnClickListener { finish() }

        btnCreateAccount!!.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {

        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        progressBar = findViewById(R.id.progressBar)
        layout = findViewById(R.id.layout)

        progressBar.getIndeterminateDrawable().setColorFilter(
            Color.rgb(0,163,81), android.graphics.PorterDuff.Mode.SRC_IN)

        etEmail?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                tiEmail.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        etPassword?.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                tiSenha.error = null
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE)

            enableViews(layout, false)

            mAuth!!
                .createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { task ->
                    progressBar.setVisibility(View.INVISIBLE)

                    enableViews(layout, true)

                    if (task.isSuccessful) {
                        val userId = mAuth!!.currentUser!!.uid

                        updateUserInfoandUi()
                    } else if (password!!.length < 6) {
                        Toast.makeText(this@CreateAccountActivity, "A senha deve ter no mínimo seis caracteres", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@CreateAccountActivity, "Já existe uma conta relacionada com esse e-mail", Toast.LENGTH_SHORT).show()
                    }
                }

        } else {
            if (TextUtils.isEmpty(email)) {
                tiEmail.error = "Preencha com o seu e-mail"
            }

            if (TextUtils.isEmpty(password)) {
                tiSenha.error = "Escolha uma senha"
            }
        }
    }

    private fun updateUserInfoandUi() {
        //Nova atividade
        val intent = Intent(this@CreateAccountActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        finish()
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
