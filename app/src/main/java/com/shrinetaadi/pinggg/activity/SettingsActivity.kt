package com.shrinetaadi.pinggg.activity

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.shrinetaadi.pinggg.Constants
import com.shrinetaadi.pinggg.R
import com.shrinetaadi.pinggg.adapter.HomeRecyclerAdapter
import com.shrinetaadi.pinggg.model.Users
import de.hdodenhof.circleimageview.CircleImageView

class SettingsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private val pickImage = 100
    private var imageUri: Uri? = null
    private lateinit var imgSettingProfile: CircleImageView
    private lateinit var imageURL: String
    private lateinit var oldimageURL: String
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        //initializing
        auth = FirebaseAuth.getInstance()
        database = Firebase.database(Constants.DATABASE_URL).reference
        storage = Firebase.storage("gs://pinggg-e4b30.appspot.com").reference

        progressLayout = findViewById(R.id.progressSLayout)
        progressBar = findViewById(R.id.progressSBar)
        val imgprogress = findViewById<ImageView>(R.id.imgSgif)
        progressBar.progress
        Glide.with(this)
            .load(R.raw.coffee)
            .into(imgprogress)

        imgSettingProfile = findViewById(R.id.imgSettingProfile)
        imgSettingProfile.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, pickImage)
        }

        val etName = findViewById<EditText>(R.id.etSettingsName)
        val etStatus = findViewById<EditText>(R.id.etSettingStatus)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                etName.setText(dataSnapshot.child("name").value.toString())
                etStatus.setText(dataSnapshot.child("status").value.toString())
                oldimageURL = dataSnapshot.child("imageURL").value.toString()
                Glide.with(this@SettingsActivity)
                    .load(dataSnapshot.child("imageURL").value.toString())
                    .into(imgSettingProfile)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.child("Details").child(auth.currentUser?.uid.toString())
            .addValueEventListener(postListener)


        val btnUpdate = findViewById<Button>(R.id.btnUpdate)
        btnUpdate.setOnClickListener {
            progressLayout.visibility =
                View.VISIBLE
            val name = etName.text.toString()
            val status = etStatus.text.toString()
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
                                            database.child("Details").child(uri)
                                                .child("name").setValue(name)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        database.child("Details").child(uri)
                                                            .child("status").setValue(status)
                                                            .addOnCompleteListener { task ->
                                                                if (task.isSuccessful) {
                                                                    database.child("Details")
                                                                        .child(uri)
                                                                        .child("imageURL")
                                                                        .setValue(imageURL)
                                                                        .addOnCompleteListener { task ->
                                                                            if (task.isSuccessful) {
                                                                                database.child("Details")
                                                                                    .addValueEventListener(
                                                                                        postListener
                                                                                    )

                                                                                Toast.makeText(
                                                                                    this,
                                                                                    "Updated Sucessfully",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                                progressLayout.visibility =
                                                                                    View.GONE

                                                                            }
                                                                        }


                                                                }
                                                            }


                                                    }
                                                }

                                        }
                                    }
                            }

                        }
                } else {
                    imageURL = oldimageURL
                    database.child("Details").child(uri)
                        .child("name").setValue(name)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                database.child("Details").child(uri)
                                    .child("status").setValue(status)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            database.child("Details")
                                                .child(uri)
                                                .child("imageURL")
                                                .setValue(imageURL)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        progressLayout.visibility =
                                                            View.GONE
                                                        Toast.makeText(
                                                            this,
                                                            "Updated Sucessfully",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }

                                                }
                                        }


                                    }

                            }

                        }
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data

            imgSettingProfile.setImageURI(imageUri)
        }
    }
}