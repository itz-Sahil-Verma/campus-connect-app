package com.example.campusconnectfinal

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.profileactivities.UserData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class register : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var storeauth: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val email = findViewById<TextInputEditText>(R.id.email)
        val password = findViewById<TextInputEditText>(R.id.password)
        val confirmpass = findViewById<TextInputEditText>(R.id.conpass)
        val btnsignUp = findViewById<Button>(R.id.btnsignup)
        val btnlogin = findViewById<Button>(R.id.btnlogin)

        //initialize firebase auth
        auth = FirebaseAuth.getInstance()
        storeauth = FirebaseFirestore.getInstance()
        progressDialog = ProgressDialog(this)

        // passing the screen when login button is click
        btnlogin.setOnClickListener {
            intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }

        val joinedCommunities = mutableMapOf<String, Boolean>()

        val communityRef = FirebaseFirestore.getInstance().collection("communities")
        communityRef.get().addOnSuccessListener { querySnapshot ->
            val allCommunities = mutableListOf<String>()
            for (document in querySnapshot.documents) {
                allCommunities.add(document.id)
            }

            // Create a map of community IDs with initial status "false"
            for (communityId in allCommunities) {
                joinedCommunities[communityId] = false
            }
        }

        // action on register button
        btnsignUp.setOnClickListener {
            val Email = email.text.toString().trim()
            val Password = password.text.toString().trim()
            val confpass = confirmpass.text.toString().trim()


            if (Email.isEmpty() ) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Password.isEmpty() ) {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confpass.isEmpty() ) {
                Toast.makeText(this, "Please Confirm Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Password != confpass) {
                Toast.makeText(this, "Password not matched", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //show progress
            progressDialog.setMessage("Creating account")
            progressDialog.show()

            auth.createUserWithEmailAndPassword(Email, Password).addOnSuccessListener{


                Toast.makeText(this, "Registrated Succesfully", Toast.LENGTH_SHORT).show()

                updateUserInfo("$Password", joinedCommunities)

            }

                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error! ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
                }


        }
    }

    private fun updateUserInfo(Password :String, joinedCommunities: Map<String, Boolean>){
        progressDialog.setMessage("Saving User Info")

        val registeredUserEmail = auth.currentUser!!.email
        val registeredUserId = auth.uid

        val userData = UserData(
            name = "",
            phoneNumber = "",
            profileImgUrl = "",
            rollNo = "",
            branch = "",
            course = "",
            onlineStatus = true,
            email = registeredUserEmail ?: "",
            uid = registeredUserId ?: "",
            password = Password,
            userType = "Email",
            joinedCommunities = joinedCommunities
        )

        // Log the data just before writing it to Firebase
        Log.d("DataToPush", "Data to push to Firebase: $userData")

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(registeredUserId!!)
            .setValue(userData)
            .addOnSuccessListener{
                progressDialog.dismiss()

                val intent = Intent(this, login::class.java)
                startActivity((intent))
                finish()
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT)
            }


    }
}