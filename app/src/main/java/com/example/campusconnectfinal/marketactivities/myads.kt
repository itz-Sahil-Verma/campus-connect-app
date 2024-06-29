package com.example.campusconnectfinal.marketactivities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.campusconnectfinal.databinding.ActivityMyadsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class myads : AppCompatActivity() {

    private lateinit var binding: ActivityMyadsBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyadsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchUserAds()
    }

    private fun fetchUserAds() {
        val userAdsRef = FirebaseDatabase.getInstance().getReference("Users/${auth.uid}/ads")
        userAdsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adIds = mutableListOf<String>()
                for (adSnapshot in snapshot.children) {
                    val adId = adSnapshot.getValue(String::class.java)
                    adId?.let { adIds.add(it) }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch user ads: $error")
            }
        })
    }






}