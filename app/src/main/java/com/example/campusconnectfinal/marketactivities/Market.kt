package com.example.campusconnectfinal.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.adapters.AdapterAd
import com.example.campusconnectfinal.adapters.ModelAd
import com.example.campusconnectfinal.databinding.FragmentMarketBinding
import com.example.campusconnectfinal.marketactivities.createad
import com.example.campusconnectfinal.marketactivities.myads
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class market : Fragment() {

    private lateinit var binding: FragmentMarketBinding
    private lateinit var adArrayList: ArrayList<ModelAd>
    private lateinit var adapterAd: AdapterAd
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMarketBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.sellbtn.setOnClickListener {

            // Create an Intent to start the new activity
            val intent = Intent(requireActivity(), createad::class.java)
            requireActivity().startActivity(intent)
        }

        binding.myads.setOnClickListener {
            val intent = Intent(requireActivity(), myads::class.java)
            requireActivity().startActivity(intent)
        }

        val layoutManager = GridLayoutManager(requireContext(), 2) // 2 columns
        binding.adsRv.layoutManager = layoutManager

        loadAds()

        return view
    }

    private fun loadAds() {

        Log.d(ContentValues.TAG, "LoadADs: ")

        adArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adArrayList.clear()

                for (ds in snapshot.children) {
                    val modelAd = ds.getValue(ModelAd::class.java)
                    adArrayList.add(modelAd!!)
                }

                //setup adapter
                adapterAd = AdapterAd(mContext, adArrayList)
                binding.adsRv.adapter = adapterAd
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}