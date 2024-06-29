package com.example.campusconnectfinal.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.adapters.joinedAdapter
import com.example.campusconnectfinal.adapters.unjoinedAdapter
import com.example.campusconnectfinal.community_data
import com.example.campusconnectfinal.databinding.FragmentCommunityBinding
import com.example.campusconnectfinal.databinding.FragmentMarketBinding
import com.example.campusconnectfinal.profileactivities.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore

class community : Fragment() {

    private lateinit var binding: FragmentCommunityBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val view = binding.root

        val layoutManager = GridLayoutManager(requireContext(), 4) // 4 columns
        binding.joinedRV.layoutManager = layoutManager

        binding.unjoinedRV.layoutManager = LinearLayoutManager(requireContext())

        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val joinedCommunitiesData = dataSnapshot.child("joinedCommunities").getValue(object : GenericTypeIndicator<Map<String, Boolean>>() {})

                    // Process joined and unjoined communities based on data
                    if (joinedCommunitiesData != null) {
                        val joinedCommunityIds = joinedCommunitiesData.filterValues { it == true }.keys.toList()
                        val unjoinedCommunityIds = joinedCommunitiesData.filterValues { it == false }.keys.toList()

                        if (joinedCommunityIds.isNotEmpty()) {
                            fetchCommunityDetails(joinedCommunityIds) { joinedList ->
                                // Callback function invoked when joined community details are fetched
                                binding.joinedRV.adapter = joinedAdapter(joinedList)
                            }
                        }

                        if (unjoinedCommunityIds.isNotEmpty()) {
                            // Fetch details of unjoined communities from Firestore
                            fetchCommunityDetails(unjoinedCommunityIds) { unjoinedList ->
                                // Callback function invoked when unjoined community details are fetched
                                binding.unjoinedRV.adapter = unjoinedAdapter(unjoinedList)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Error fetching user data: $error")
            }
        })

        return view
    }

    private fun fetchCommunityDetails(CommunityIds: List<String>, callback: (List<community_data>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val communitiesRef = db.collection("communities")

        val query = communitiesRef.whereIn("communityId", CommunityIds)

        query.get().addOnSuccessListener { querySnapshot ->
            val joinedCommunitiesList = mutableListOf<community_data>()
            for (documentSnapshot in querySnapshot.documents) {
                val community = documentSnapshot.toObject(community_data::class.java)
                if (community != null) {
                    joinedCommunitiesList.add(community)
                }
            }

            // Update your joined communities RecyclerView adapter with fetched data
            callback(joinedCommunitiesList)

        }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error fetching community details: $exception")
            }
    }
}