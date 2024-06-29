package com.example.campusconnectfinal.marketactivities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.databinding.ActivityMainBinding
import com.example.campusconnectfinal.databinding.ActivityProductInfoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class product_info : AppCompatActivity() {

    private lateinit var binding : ActivityProductInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backbtn.setOnClickListener {
            onBackPressed()
        }



        getProductDetails(intent.getStringExtra("id"))
    }

    private fun getProductDetails(proid: String?) {
        val reference = FirebaseDatabase.getInstance().reference.child("Ads").child(proid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val title = snapshot.child("title").value.toString()
                        val price = snapshot.child("price").value.toString()
                        val des = snapshot.child("des").value.toString()
                        val used = snapshot.child("used").value.toString()

                        // Set retrieved data to TextViews
                        binding.producttv.text = title
                        binding.pricetv.text = price
                        binding.destv.text = des
                        binding.usedtv.text = used

                        // Check if "images" path exists
                        if (snapshot.hasChild("images")) {
                            val imageSnapshot = snapshot.child("images")

                            // Check if "imgurl" field exists within "images"
                            if (imageSnapshot.hasChild("imgurl")) {
                                val imageUrl =
                                    imageSnapshot.child("imgurl").getValue(String::class.java)

                                if (imageUrl != null) {
                                    // Load image using Glide
                                    Glide.with(this@product_info)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.baseline_image_24) // Placeholder image while loading
                                        .into(binding.imgv) // Assuming you have an ImageView with id 'imgv'
                                } else {
                                    // Handle case where image URL is null (optional: display default image)
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this@product_info, "Product does not exist", Toast.LENGTH_SHORT).show()
                    }
                }

                // Implement onCancelled method for error handling
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@product_info, "Failed to retrieve data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    }