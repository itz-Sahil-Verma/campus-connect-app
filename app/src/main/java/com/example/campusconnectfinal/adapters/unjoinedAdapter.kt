package com.example.campusconnectfinal.adapters

import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.community_data
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class unjoinedAdapter(private val communitiesList: List<community_data>) : RecyclerView.Adapter<unjoinedAdapter.CommunityViewHolder>() {

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define views in the layout
        private val communityNameTextView: TextView = itemView.findViewById(R.id.c_name_tv)
        private val joinbtn : Button = itemView.findViewById(R.id.joinbtn)

        fun bind(community: community_data) {
            // Bind data to views
            communityNameTextView.text = community.name
            // Assuming memberCount is an integer and string resource for "members_count" is defined

            joinbtn.setOnClickListener {
                // Show progress dialog
                val progressDialog = ProgressDialog(itemView.context)
                progressDialog.setMessage("Joining community...")
                progressDialog.setCancelable(false)
                progressDialog.show()

                // Update status in the database
                val communityId = community.communityId
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId).child("joinedCommunities").child(communityId)
                userRef.setValue(true)
                    .addOnCompleteListener { task ->
                        // Dismiss progress dialog
                        progressDialog.dismiss()

                        if (task.isSuccessful) {
                            // Show toast indicating successful join
                            Toast.makeText(itemView.context, "Joined", Toast.LENGTH_SHORT).show()
                        } else {
                            // Show toast indicating failure
                            Toast.makeText(itemView.context, "Failed to join", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        // Inflate the layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.unjoined, parent, false)
        return CommunityViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommunityViewHolder, position: Int) {
        // Bind data to the ViewHolder
        holder.bind(communitiesList[position])
    }

    override fun getItemCount(): Int {
        // Return the size of the list
        return communitiesList.size
    }
}