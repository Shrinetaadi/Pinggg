package com.shrinetaadi.pinggg.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.shrinetaadi.pinggg.R
import com.shrinetaadi.pinggg.model.Users
import com.shrinetaadi.pinggg.activity.ChatActivity
import de.hdodenhof.circleimageview.CircleImageView

class HomeRecyclerAdapter(val context: Context, val userList: ArrayList<Users>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return HomeViewHolder(v, context)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(userList[position])


    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class HomeViewHolder(itemView: View, val context: Context) : RecyclerView.ViewHolder(itemView) {

        fun bind(user: Users) {
            val txtName = itemView.findViewById<TextView>(R.id.txtUsername)
            val txtStatus = itemView.findViewById<TextView>(R.id.txtStatus)
            val imgProfile = itemView.findViewById<CircleImageView>(R.id.imgUser)
            txtName.text = user.name.capitalize()
            txtStatus.text = user.status.capitalize()
            Glide.with(itemView).load(user.imageURL).into(imgProfile)
            itemView.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("name", user.name.capitalize())
                intent.putExtra("image", user.imageURL)
                intent.putExtra("uid", user.uri)
                context.startActivity(intent)
            }

        }
    }
}


