package com.example.storage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso


class Adapter(val context: Context, val Posts: MutableList<PostData>) :
    RecyclerView.Adapter<Adapter.AdapterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_element, parent, false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        val currentPost = Posts[position]
        holder.key.text = currentPost.key
        holder.caption.text = currentPost.caption

        FirebaseStorage.getInstance().reference.child("images").downloadUrl.addOnSuccessListener {
            Picasso.get().load(currentPost.uri).into(holder.postImage)
        }
    }

    override fun getItemCount(): Int {
        return Posts.size
    }

    inner class AdapterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val key = view.findViewById<TextView>(R.id.key)
        val caption = view.findViewById<TextView>(R.id.caption)
        val postImage = view.findViewById<ImageView>(R.id.postImage)

    }


}