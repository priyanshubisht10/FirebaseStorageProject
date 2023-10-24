package com.example.storage

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.storage.databinding.ActivityPostBinding
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import kotlin.random.Random

val storage = FirebaseStorage.getInstance()
val storageRef: StorageReference = storage.reference

class PostActivity : AppCompatActivity() {
    private lateinit var selectImg: Button
    private lateinit var imageView: ImageView
    private lateinit var feedActivitybtn: Button
    private lateinit var binding: ActivityPostBinding
    private lateinit var captionEdittext: EditText

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPostBinding.inflate(layoutInflater)

        setContentView(binding.root)

        selectImg = findViewById(R.id.select_img_btn)
        imageView = findViewById(R.id.gallery_image)
        feedActivitybtn = findViewById(R.id.feedActivity)

        selectImg.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestStoragePermission()
            } else {
                val pickIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                opengalleryLauncher.launch(pickIntent)
            }
        }

        feedActivitybtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // finish()  // Uncomment this line if you want to finish the current activity
        }



        binding.postImgBtn.setOnClickListener {
            captionEdittext = findViewById(R.id.post_description)
            val caption: String = captionEdittext.text.toString()
            val imageName: String = generateRandomString(
                10,
                "qwertyuiopasdfghjklzxcvbnm,QWERTYUIOPASDFGHJKLZXCVBNM,.1234567890"
            )
            uploadImage(
                imageView,
                imageName,
                caption
            )
//            uploadData(imageName,captionEdittext.toString())
        }

    }

    private val opengalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            findViewById<ImageView>(R.id.gallery_image).setImageURI(result.data?.data)
        }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted && permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    opengalleryLauncher.launch(pickIntent)
                } else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog()
        } else {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun showRationaleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Storage Permission").setMessage("Allow us to read your internal storage")
            .setPositiveButton("Yes") { dialog, _ ->
                requestPermission.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun uploadImage(imageView: ImageView, imageName: String,caption: String) {
        // Get the drawable from the ImageView
        val drawable = imageView.drawable
        if (drawable != null) {
            val bitmap = (drawable as BitmapDrawable).bitmap

            // Convert the bitmap to a byte array
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            // Create a reference to the image in Firebase Storage
            val imageRef: StorageReference = storageRef.child("images/$imageName.jpg")


            // Upload the image
            val uploadTask: UploadTask = imageRef.putBytes(data)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // Get the download URL (URI) of the uploaded image
                    val downloadUrl = uri.toString()
                    Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()

                    val post: DocumentReference = db.collection("Posts").document()
                    val postInfo = mapOf(
                        "key" to imageName,
                        "caption" to caption,
                        "imageUri" to downloadUrl
                    )
                    post.set(postInfo)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(
                                this,
                                "Post Data Uploaded Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                e.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    // You can now use the download URL as needed (e.g., store it in a database or display it to the user)
                    // For example, you can save the URL to a Firestore database, if you're using Firestore.
                }.addOnFailureListener { exception ->
                    Toast.makeText(this, "Image Not Uploaded Successfully", Toast.LENGTH_SHORT)
                        .show()
                    // Handle any errors that may occur when getting the download URL
                }
            }.addOnFailureListener { exception ->
                // Handle any errors that may occur during the upload
                Toast.makeText(this, "Image Not Uploaded Successfully", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun generateRandomString(length: Int, characterPool: String): String {
        val random = Random.Default
        return (1..length)
            .map { characterPool[random.nextInt(0, characterPool.length)] }
            .joinToString("")
    }

//    fun uploadData(imageName: String,caption: String ) {
//        val post: DocumentReference = db.collection("Posts").document()
//        val postInfo = mapOf(
//            "key" to imageName,
//            "caption" to caption,
//            "imageUri" to downloadUrl
//        )
//        post.set(postInfo)
//            .addOnSuccessListener { documentReference ->
//                Toast.makeText(
//                    this,
//                    "Post Data Uploaded Successfully",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(
//                    this,
//                    e.message,
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//    }
}
