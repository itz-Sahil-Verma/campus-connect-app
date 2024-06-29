package com.example.campusconnectfinal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.community_data
import de.hdodenhof.circleimageview.CircleImageView

class joinedAdapter(private val communitiesList: List<community_data>) : RecyclerView.Adapter<joinedAdapter.CommunityViewHolder>() {

    inner class CommunityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define views in the layout
        private val communityNameTextView: TextView = itemView.findViewById(R.id.tv_name)

        fun bind(community: community_data) {
            // Bind data to views
            communityNameTextView.text = community.name
            // Assuming memberCount is an integer and string resource for "members_count" is defined
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityViewHolder {
        // Inflate the layout for each item
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.joined, parent, false)
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

