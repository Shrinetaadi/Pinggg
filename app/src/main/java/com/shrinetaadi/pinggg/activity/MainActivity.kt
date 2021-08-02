package com.shrinetaadi.pinggg.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.shrinetaadi.pinggg.R

lateinit var leftAnim: Animation
lateinit var rightAnim: Animation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var imgLogo = findViewById<ImageView>(R.id.imgLogo)
        var txtName = findViewById<TextView>(R.id.txtName)
        leftAnim = AnimationUtils.loadAnimation(this, R.anim.left_animation)
        rightAnim = AnimationUtils.loadAnimation(this, R.anim.right_animation)

        imgLogo.animation = leftAnim
        txtName.animation = rightAnim
        Handler().postDelayed({
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

    }
}