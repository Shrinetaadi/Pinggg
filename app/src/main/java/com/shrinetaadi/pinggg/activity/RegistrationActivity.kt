package com.shrinetaadi.pinggg.activity

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.shrinetaadi.pinggg.Constants
import com.shrinetaadi.pinggg.R
import com.shrinetaadi.pinggg.model.Users
import de.hdodenhof.circleimageview.CircleImageView


class RegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference

    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar

    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var imgProfile: CircleImageView
    private lateinit var imageURL: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        //href links
        setupActivityLink()

        //initializing
        auth = FirebaseAuth.getInstance()
        database = Firebase.database(Constants.DATABASE_URL).reference
        storage = Firebase.storage("gs://pinggg-e4b30.appspot.com").reference


        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        val etName = findViewById<EditText>(R.id.etSUName)
        val etEmail = findViewById<EditText>(R.id.etSUEmail)
        val etPassword = findViewById<EditText>(R.id.etSUPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etSUConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val imgprogress = findViewById<ImageView>(R.id.imggif)
        imgProfile = findViewById(R.id.imgProfile)

        progressBar.progress
        Glide.with(this)
            .load(R.raw.coffee)
            .into(imgprogress)

        imgProfile.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }


        btnSignUp.setOnClickListener {
            progressLayout.visibility =
                View.VISIBLE
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val status = "This is the First Status Please Change"

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
                etEmail.error = "Invalid Email"
                Toast.makeText(
                    baseContext, "Enter Valid Email",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!(password.length > 6)) {
                progressLayout.visibility =
                    View.GONE
                etPassword.error = "Invalid Password"
                Toast.makeText(
                    baseContext, "Enter Valid Password",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!(password == confirmPassword)) {
                progressLayout.visibility =
                    View.GONE
                etPassword.error = "Password Does not Match"
                etConfirmPassword.error = "Password Does not Match"
                Toast.makeText(
                    baseContext, "Check the Password",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val uri = auth.currentUser?.uid

                            if (uri != null) {
                                if (imageUri != null) {
                                    storage.child("ProfileImage").child(uri).child("Image")
                                        .putFile(imageUri!!).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                storage.child("ProfileImage").child(uri)
                                                    .child("Image").downloadUrl.addOnCompleteListener { task ->
                                                        if (task.isSuccessful) {
                                                            imageURL = task.result.toString()
                                                            val users =
                                                                Users(
                                                                    uri,
                                                                    name,
                                                                    email,
                                                                    imageURL, status
                                                                )
                                                            database.child("Details").child(uri)
                                                                .setValue(users)
                                                                .addOnCompleteListener { task ->


                                                                    if (task.isSuccessful) {
                                                                        progressLayout.visibility =
                                                                            View.GONE
                                                                        startActivity(
                                                                            Intent(
                                                                                this,
                                                                                HomeActivity::class.java
                                                                            )
                                                                        )


                                                                        Toast.makeText(
                                                                            this,
                                                                            "Successfully Registered",
                                                                            Toast.LENGTH_LONG
                                                                        )
                                                                            .show()
                                                                    }
                                                                }

                                                        }
                                                    }
                                            }

                                        }
                                } else {
                                    imageURL =
                                        "https://firebasestorage.googleapis.com/v0/b/pinggg-e4b30.appspot.com/o/profile.png?alt=media&token=afaf60e8-ca36-4a5c-9847-bf6c6e42489a"
                                    val users = Users(uri, name, email, imageURL, status)
                                    database.child("Details").child(uri).setValue(users)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                progressLayout.visibility =
                                                    View.GONE
                                                startActivity(
                                                    Intent(
                                                        this,
                                                        HomeActivity::class.java
                                                    )
                                                )
                                                Toast.makeText(
                                                    this,
                                                    "Successfully Registered",
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }

                                        }
                                }


                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(ContentValues.TAG, "signUpWithEmail:failure", task.exception)
                            Toast.makeText(
                                baseContext, task.exception.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }


        }
    }



    private fun setupActivityLink() {
        val linkTextView = findViewById<TextView>(R.id.signInLink)
        linkTextView.setTextColor(Color.RED)
        linkTextView.setOnClickListener {
            val switchActivityIntent = Intent(this, LoginActivity::class.java)
            startActivity(switchActivityIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data

            imgProfile.setImageURI(imageUri)
        }
    }


}