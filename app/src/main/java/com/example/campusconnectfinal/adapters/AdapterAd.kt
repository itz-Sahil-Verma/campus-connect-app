package com.example.campusconnectfinal.adapters

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.databinding.RowadBinding
import com.example.campusconnectfinal.marketactivities.product_info
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class AdapterAd : androidx.recyclerview.widget.RecyclerView.Adapter<AdapterAd.HolderAd> {

    private lateinit var binding: RowadBinding

    private var context: Context
    private var adArrayList: ArrayList<ModelAd>
    private var firebaseAuth: FirebaseAuth

    constructor(context: Context, adArrayList: ArrayList<ModelAd>){
        this.context = context
        this.adArrayList = adArrayList
        firebaseAuth = FirebaseAuth.getInstance()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderAd {
//       inflate/binnd the rowad.xml
        binding = RowadBinding.inflate(LayoutInflater.from(context), parent, false)

        return HolderAd(binding.root)
    }

    override fun onBindViewHolder(holder: HolderAd, position: Int) {
//        get data from particular position of list and set to the ui views of row ad xml and handle clicks

        val modelAd = adArrayList[position]

        val title = modelAd.title
        val price = "₹ ${modelAd.price}"

        loadFirstImage(modelAd, holder)

        holder.titleTv.text = title
        holder.priceTv.text = price

        holder.itemView.setOnClickListener {
            val intent = Intent(context, product_info::class.java)
            intent.putExtra( "id", adArrayList[position].id)
            context.startActivity(intent)
        }
    }



    private fun loadFirstImage(modelAd: ModelAd, holder: HolderAd) {

        //load first image from available images of Ad
        // ad id to get image of it

        val adId = modelAd.id

        Log.d(ContentValues.TAG, "LoadAdFirstImage: adid: $adId")

        val reference = FirebaseDatabase.getInstance().getReference( "Ads")
        reference.child(adId).child("images").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val imageUrl = snapshot.child("imgurl").value.toString()
                    Log.d(ContentValues.TAG, "onDataChange: imgurl: $imageUrl")

                    try {
                        Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.baseline_image_24)
                            .into(holder.imageIv)
                    } catch (e: Exception) {
                        Log.e(ContentValues.TAG, "Glide load exception: ${e.message}")
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "Database error: ${error.message}")
            }
        })

    }

    override fun getItemCount(): Int {
//        return the size of the list
        return adArrayList.size
    }



    inner class HolderAd(itemView: View): RecyclerView.ViewHolder(itemView){

        //init ui views of the rowad xml

        var imageIv = binding.productImage
        var titleTv = binding.productName
        var priceTv = binding.productPrice
    }

}

