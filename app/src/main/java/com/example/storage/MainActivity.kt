package com.example.storage

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {

    private lateinit var postRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        postRecyclerView = findViewById(R.id.recyclerView)

        getUriList("Posts",
            uriValuesLoaded = {
                val uriList = it
                println(uriList)

                val Posts = mutableListOf<PostData>()
                for (i in 0 until uriList.size) {
                    Posts.add(PostData("key${i + 1}", "Hey!", uriList[i]))
                }

                postRecyclerView.adapter = Adapter(this, Posts)
                postRecyclerView.layoutManager = LinearLayoutManager(this)

            }, onError = {
                Toast.makeText(
                    this@MainActivity,
                    "Error generated while making list",
                    Toast.LENGTH_SHORT
                ).show()
            })

    }

//    private fun generateList(size: Int): MutableList<PostData> {
//        val list = mutableListOf<PostData>()
//        for (i in 0 until 10) {
//            list.add(PostData("key${i + 1}", "Hey!"),)
//        }
//        return list
//    }

    private fun getUriList(
        collectionPath: String,
        uriValuesLoaded: (List<String>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val db = FirebaseFirestore.getInstance()
        val uriList = mutableListOf<String>()

        db.collection(collectionPath)
            .get()
            .addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                querySnapshot?.documents?.forEach { document ->
                    val uri = document.getString("imageUri")
                    if (uri != null) {
                        uriList.add(uri)
                    }
                }
                uriValuesLoaded(uriList)
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }
}