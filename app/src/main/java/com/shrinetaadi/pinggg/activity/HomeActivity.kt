package com.shrinetaadi.pinggg.activity

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.shrinetaadi.pinggg.adapter.HomeRecyclerAdapter
import com.shrinetaadi.pinggg.R
import com.shrinetaadi.pinggg.model.Users


class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        auth = FirebaseAuth.getInstance()
        progressLayout = findViewById(R.id.progressHLayout)
        progressBar = findViewById(R.id.progressHBar)
        val imgprogress = findViewById<ImageView>(R.id.imgHgif)
        progressBar.progress
        Glide.with(this)
            .load(R.raw.coffee)
            .into(imgprogress)
        progressLayout.visibility =
            View.VISIBLE

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            progressLayout.visibility =
                View.GONE
        } else if (auth.currentUser != null) {
            database = Firebase.database(Constants.DATABASE_URL).reference.child("Details")
            storage =
                Firebase.storage("gs://pinggg-e4b30.appspot.com").reference.child("ProfileImage")
                    .child(auth.currentUser?.uid.toString())
                    .child("Image")
            recyclerView = findViewById(R.id.recViewHome)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    recyclerView.layoutManager = LinearLayoutManager(this@HomeActivity)
                    // Get Post object and use the values to update the UI
                    val userList = ArrayList<Users>()
                    dataSnapshot.children.forEach {
                        val userdata = it.getValue(Users::class.java)
                        if (userdata?.uri != auth.uid) {
                            userList.add(userdata!!)
                        }
                    }
                    val adapter = HomeRecyclerAdapter(this@HomeActivity, userList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    progressLayout.visibility =
                        View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.addValueEventListener(postListener)


            val logoutbtn = findViewById<ImageView>(R.id.imgLogOut)
            logoutbtn.setOnClickListener {
                val dialog = Dialog(this@HomeActivity, R.style.Dialoge)
                dialog.setContentView(R.layout.logout_dialoge)
                val img = dialog.findViewById<ImageView>(R.id.img)
                Glide.with(this).load(R.drawable.iconaa).into(img)

                val yesbtn = dialog.findViewById<Button>(R.id.btndiaYes)
                val nobtn = dialog.findViewById<Button>(R.id.btndiaNo)

                yesbtn.setOnClickListener {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this@HomeActivity, LoginActivity::class.java))

                }
                nobtn.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()


            }
            val imgSetting = findViewById<ImageView>(R.id.imgSettings)
            imgSetting.setOnClickListener {
                startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
            }

        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}