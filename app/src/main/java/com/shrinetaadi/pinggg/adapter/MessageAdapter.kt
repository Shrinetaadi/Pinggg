package com.shrinetaadi.pinggg.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.shrinetaadi.pinggg.model.Message
import com.shrinetaadi.pinggg.R
import de.hdodenhof.circleimageview.CircleImageView

class MessageAdapter(
    val context: Context,
    val messages: ArrayList<Message>,
    val simageUrl: String,
    val rimageUrl: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    val ITEM_SEND = 1
    val ITEM_RECEIVE = 2


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        if (viewType == ITEM_SEND) {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.sender_message_item, parent, false)
            return SenderViewHolder(v, context, simageUrl)
        } else {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.receiver_message_item, parent, false)
            return ReceiverViewHolder(v, context, rimageUrl)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_SEND) {
            (holder as SenderViewHolder).bindSend(messages[position])
        } else {
            (holder as ReceiverViewHolder).bindRecieve(messages[position])
        }

    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(message.senderId)) {
            return ITEM_SEND

        } else {
            return ITEM_RECEIVE
        }
    }


    inner class SenderViewHolder(
        itemView: View, val context: Context, val simageUrl: String
    ) :
        RecyclerView.ViewHolder(itemView) {

        fun bindSend(message: Message) {
            val txtMessage = itemView.findViewById<TextView>(R.id.txtSMessage)
            val imgProfile = itemView.findViewById<CircleImageView>(R.id.imgSmesProfile)

            txtMessage.text = message.message
            Glide.with(itemView).load(simageUrl).into(imgProfile)
        }

    }

    inner class ReceiverViewHolder(
        itemView: View,
        val context: Context,
        val rimageUrl: String
    ) :
        RecyclerView.ViewHolder(itemView) {

        fun bindRecieve(message: Message) {
            val txtMessage = itemView.findViewById<TextView>(R.id.txtRMessage)
            val imgProfile = itemView.findViewById<CircleImageView>(R.id.imgRmesProfile)
            txtMessage.text = message.message
            Glide.with(itemView).load(rimageUrl).into(imgProfile)
        }

    }
}



