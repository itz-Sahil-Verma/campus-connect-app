package com.example.campusconnectfinal.marketactivities

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campusconnectfinal.adapters.AdapterAd
import com.example.campusconnectfinal.adapters.ModelAd
import com.example.campusconnectfinal.adapters.MyAdadapter
import com.example.campusconnectfinal.databinding.ActivityMyadsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.auth.User

class myads : AppCompatActivity() {

    private lateinit var binding: ActivityMyadsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var Useradlist: ArrayList<ModelAd>
    private lateinit var myAdadapter: MyAdadapter
    private lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyadsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        mContext = this

        binding.backbtn.setOnClickListener {
            onBackPressed()
        }

        Useradlist = ArrayList()
        myAdadapter = MyAdadapter(mContext, Useradlist)
        binding.myAdRv.adapter = myAdadapter
        binding.myAdRv.layoutManager = LinearLayoutManager(mContext)

        fetchUserAds()
    }

    private fun fetchUserAds() {
        Log.d(TAG, "Fetch User Ads")

        val userAdsRef = FirebaseDatabase.getInstance().getReference("Users/${auth.uid}/ads")
        userAdsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val adId = ds.getValue(String::class.java)
                    adId?.let {
                        fetchAdDetails(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch user ads: $error")
            }
        })
    }

    private fun fetchAdDetails(adId: String) {
        val adsRef = FirebaseDatabase.getInstance().getReference("Ads").child(adId)
        adsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val modelAd = snapshot.getValue(ModelAd::class.java)
                modelAd?.let {
                    Useradlist.add(it)
                    myAdadapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch ad details: $error")
            }
        })
    }
}









