package com.shrinetaadi.pinggg.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
import com.shrinetaadi.pinggg.*
import com.shrinetaadi.pinggg.adapter.MessageAdapter
import com.shrinetaadi.pinggg.model.Message
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var rname: String
    private lateinit var ruid: String
    lateinit var rimageurl: String
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    public var simageurl: String = ""
    private lateinit var suid: String
    private lateinit var sroom: String
    private lateinit var rroom: String
    private lateinit var recylerview: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = FirebaseAuth.getInstance()
        suid = auth.uid.toString()
        database = Firebase.database(Constants.DATABASE_URL).reference

        database.child("Details").child(suid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                simageurl = snapshot.child("imageURL").getValue().toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        val imgChatProfile = findViewById<CircleImageView>(R.id.imgChatProfile)
        val txtChatName = findViewById<TextView>(R.id.txtChatName)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnMessage = findViewById<ImageView>(R.id.btnMessage)
        recylerview = findViewById(R.id.recViewChat)




        if (intent != null) {
            rname = intent?.getStringExtra("name").toString()
            rimageurl = intent?.getStringExtra("image").toString()
            ruid = intent?.getStringExtra("uid").toString()
        }
        sroom = suid + ruid
        rroom = ruid + suid
        Glide.with(this).load(rimageurl).into(imgChatProfile)
        txtChatName.text = rname


        val chatref = database.child("chats").child(sroom).child("messages")
        chatref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList: ArrayList<Message> = ArrayList()
                for (data in snapshot.children) {
                    val messageData = data.getValue<Message>(Message::class.java)
                    val message = messageData?.let { it } ?: continue
                    messageList.add(message)
                }


                setupAdapter(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })




        btnMessage.setOnClickListener {

            val message = etMessage.text.toString()
            val date = Date()
            val messages = Message(message, suid, date.time)
            etMessage.text.clear()


            database = Firebase.database(Constants.DATABASE_URL).reference
            database.child("chats").child(sroom).child("messages").push().setValue(messages)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        database.child("chats").child(rroom).child("messages").push()
                            .setValue(messages)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {

                                }

                            }
                    }

                }

        }
        val imgSetting = findViewById<ImageView>(R.id.imgSettings)
        imgSetting.setOnClickListener {
            startActivity(Intent(this@ChatActivity, SettingsActivity::class.java))
        }


    }

    private fun setupAdapter(data: ArrayList<Message>) {
        val linearLayoutManager = LinearLayoutManager(this)
        recylerview.layoutManager = linearLayoutManager
        recylerview.adapter = MessageAdapter(this, data, simageurl, rimageurl)

        //scroll to bottom

        recylerview.scrollToPosition(data.size - 1)
    }
}