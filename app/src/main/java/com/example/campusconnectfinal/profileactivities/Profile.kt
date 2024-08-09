package com.example.campusconnectfinal.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.databinding.FragmentProfileBinding
import com.example.campusconnectfinal.login
import com.example.campusconnectfinal.profileactivities.edit_profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class profile : Fragment() {

    private lateinit var mContext: Context
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: FragmentProfileBinding

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        auth = FirebaseAuth.getInstance()

        val btnlogout = view.findViewById<CardView>(R.id.logoutCv)
        val nametv = view.findViewById<TextView>(R.id.nameTv)
        val emailtv = view.findViewById<TextView>(R.id.emailTv)
        val phonetv = view.findViewById<TextView>(R.id.phoneTv)
        val coursetv = view.findViewById<TextView>(R.id.courseTv)
        val branchtv = view.findViewById<TextView>(R.id.branchTv)


        loadmyinfo()


        btnlogout?.setOnClickListener {
            val sharedPreferences: SharedPreferences = mContext.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("isLoggedIn", false)
            editor.apply()


            auth.signOut()
            val intent = Intent(context, login::class.java)
            startActivity(intent)
            activity?.finish()
        }

        //handle editprofile , start profileEditActivity
        binding.EditProfileCv.setOnClickListener {
            startActivity(Intent(mContext, edit_profile::class.java))
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Reload user information when the fragment is resumed
        loadmyinfo()
    }

    private fun loadmyinfo(){

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${auth.uid}")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot){

                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val phone = "${snapshot.child("phoneNumber").value}"
                    val branch = "${snapshot.child("branch").value}"
                    val rollno = "${snapshot.child("rollNo").value}"
                    val course = "${snapshot.child("course").value}"
                    val profileimgurl = "${snapshot.child("profileImgUrl").value}"
                    val usertype = "${snapshot.child("usertype").value}"

                    //set data to ui

                    binding.emailTv.text = email
                    binding.nameTv.text = name
                    binding.rollTv.text = rollno
                    binding.branchTv.text = branch
                    binding.phoneTv.text= phone
                    binding.courseTv.text = course


                    if(usertype == "Email"){

                        val isVerified = auth.currentUser!!.isEmailVerified

                        if(isVerified){
                            binding.statusTv.text = "Verified0"
                        }

                        else{
                            binding.statusTv.text = "Not Verified"
                        }
                    }

                    else{
                        binding.statusTv.text = "Verified"
                    }

                    //set profile image to profiletv

                    try{
                        Glide.with(mContext)
                            .load(profileimgurl)
                            .placeholder(R.drawable.baseline_person_24_white)
                            .into(binding.profileIv)
                    }
                    catch (e: Exception){
                        //failed to set images
                        Log.e(ContentValues.TAG, "onDataChange: ", e)

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

}