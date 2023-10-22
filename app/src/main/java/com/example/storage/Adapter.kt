package com.example.storage

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Adapter(val context: Context, val Posts: MutableList<PostData>) : RecyclerView.Adapter<Adapter.AdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter.AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_element,parent,false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: Adapter.AdapterViewHolder, position: Int) {
        val currentPost = Posts[position]
        holder.key.text = currentPost.key
        holder.caption.text = currentPost.caption
    }

    override fun getItemCount(): Int {
        return Posts.size
    }

    inner class AdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val key = view.findViewById<TextView>(R.id.key)
        val caption = view.findViewById<TextView>(R.id.caption)
        //val postImage = view.findViewById<ImageView>(R.id.postImage)



    }


}