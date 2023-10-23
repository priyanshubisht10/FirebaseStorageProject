package com.example.storage

import android.os.Bundle
import android.widget.Adapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var postRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postRecyclerView = findViewById(R.id.recyclerView)

        val Posts = generateList(10)

        postRecyclerView.adapter = Adapter(this,Posts)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun generateList(size: Int) : MutableList<PostData> {
        val list = mutableListOf<PostData>()
        for(i in 0 until 10) {
            list.add(PostData("key${i+1}","Hey!"))
        }
        return list
    }
}