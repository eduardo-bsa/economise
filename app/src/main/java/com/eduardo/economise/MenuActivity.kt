package com.eduardo.economise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_menu.*


class MenuActivity : AppCompatActivity() {

    lateinit var btEmail: Button
    lateinit var tvLogin: TextView
    lateinit var imLogin: ImageView
    lateinit var imEmail:ImageView
    lateinit var google_login_button: Button
    //lateinit var imFace: ImageView
    private lateinit var auth: FirebaseAuth

    var callbackManager: CallbackManager?=null

    var googleSignInClient : GoogleSignInClient? = null
    val RC_SIGN_IN = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initialise()
    }

    private fun initialise() {
        btEmail = findViewById(R.id.btEmail)
        tvLogin = findViewById(R.id.tvLogin)
        imLogin = findViewById(R.id.imLogin)
        imEmail = findViewById(R.id.imEmail)
        //imFace = findViewById(R.id.imFace)
        google_login_button = findViewById(R.id.google_login_button)

        auth = FirebaseAuth.getInstance()

        callbackManager = CallbackManager.Factory.create()
        btn_login.setReadPermissions("email")

        btn_login.setOnClickListener { signInFace() }

        imEmail.bringToFront()
        //imFace.bringToFront()

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

        val gsq = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gsq)

        google_login_button.setOnClickListener {
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Log.w("TAG", "Google sign in failed", e)
            }
        } else {
            callbackManager!!.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(acct?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val intent = Intent(this@MenuActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "erro", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signInFace() {
        btn_login.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
            override fun onSuccess(result: LoginResult?) {
                handleFacebookAccessToken(result!!.accessToken)
            }

            override fun onCancel() {
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        auth.signInWithCredential(credential)
            .addOnFailureListener {e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnSuccessListener {result ->
                val intent = Intent(this@MenuActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
    }
}
