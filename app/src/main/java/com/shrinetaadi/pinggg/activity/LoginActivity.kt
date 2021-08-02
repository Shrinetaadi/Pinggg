package com.shrinetaadi.pinggg.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shrinetaadi.pinggg.R

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupActivityLink()
        auth = FirebaseAuth.getInstance()
        progressLayout = findViewById(R.id.progressLLayout)
        progressBar = findViewById(R.id.progressLBar)
        val etemail = findViewById<EditText>(R.id.etEmail)
        val etpassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnSignIn)
        val imgprogress = findViewById<ImageView>(R.id.imgLgif)
        progressBar.progress
        Glide.with(this)
            .load(R.raw.coffee)
            .into(imgprogress)

        btnLogin.setOnClickListener {

            progressLayout.visibility =
                View.VISIBLE
            val email = etemail.text.toString()
            val password = etpassword.text.toString()

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                progressLayout.visibility =
                    View.GONE
                Toast.makeText(
                    baseContext, "Enter Valid Data.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                progressLayout.visibility =
                    View.GONE
                etemail.error = "Invalid Email"
                Toast.makeText(
                    baseContext, "Enter Valid Email",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!(password.length > 6)) {
                progressLayout.visibility =
                    View.GONE
                etpassword.error = "Invalid Password"
                Toast.makeText(
                    baseContext, "Enter Valid Password",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->

                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            progressLayout.visibility =
                                View.GONE
                            startActivity(Intent(this, HomeActivity::class.java))
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }


    }

    private fun setupActivityLink() {
        val linkTextView = findViewById<TextView>(R.id.signUpLink)
        linkTextView.setTextColor(Color.BLUE)
        linkTextView.setOnClickListener {
            val switchActivityIntent = Intent(this, RegistrationActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }


}