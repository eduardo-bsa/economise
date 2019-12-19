package com.eduardo.economise

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CreateAccountActivity : AppCompatActivity() {

    //UI

    private var etFirstName: EditText? = null
    private var etLastName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    lateinit var layout: View
    lateinit var progressBar: ProgressBar

    //Referências ao BD

    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"

    //Variáveis globais

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        initialise()
    }

    private fun initialise() {
        etFirstName = findViewById(R.id.et_first_name) as EditText
        etLastName = findViewById(R.id.et_last_name) as EditText
        etEmail = findViewById(R.id.et_email) as EditText
        etPassword = findViewById(R.id.et_password) as EditText
        btnCreateAccount = findViewById(R.id.btn_register) as Button

        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference!!.child("users")
        mAuth = FirebaseAuth.getInstance()

        btnCreateAccount!!.setOnClickListener { createNewAccount() }
    }

    private fun createNewAccount() {

        firstName = etFirstName?.text.toString()
        lastName = etLastName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        progressBar = findViewById(R.id.progressBar)
        layout = findViewById(R.id.layout)

        progressBar.getIndeterminateDrawable().setColorFilter(
            Color.rgb(0,126,0), android.graphics.PorterDuff.Mode.SRC_IN)

        if (!TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressBar.setVisibility(View.VISIBLE)

            enableViews(layout, false)

            mAuth!!
                .createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener(this) { task ->
                    progressBar.setVisibility(View.INVISIBLE)

                    enableViews(layout, true)

                    if (task.isSuccessful) {
                        Log.d(TAG, "CreateUserWithEmail:Sucess")

                        val userId = mAuth!!.currentUser!!.uid

                        val currentUserDb = mDatabaseReference!!.child(userId)
                        currentUserDb.child("firstName").setValue(firstName)
                        currentUserDb.child("lastName").setValue(lastName)

                        updateUserInfoandUi()
                    } else {
                        Log.w(TAG, "CreateUserWithEmail:Fail", task.exception)
                        Toast.makeText(this@CreateAccountActivity, "A senha deve ter no mínimo seis caracteres", Toast.LENGTH_SHORT).show()
                    }
                }

        } else {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
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
