package com.example.campusconnectfinal

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.campusconnectfinal.MainActivity
import com.example.campusconnectfinal.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressDialog = ProgressDialog(this)
        auth = FirebaseAuth.getInstance()

        // making google pop up
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val email = findViewById<TextInputEditText>(R.id.email)
        val password = findViewById<TextInputEditText>(R.id.pass)
        val btnregister = findViewById<Button>(R.id.btnregister)
        val btnlogin = findViewById<Button>(R.id.btnlogin)
        val btngoole = findViewById<LinearLayout>(R.id.googlesignin)

        btnregister.setOnClickListener {

            intent = Intent(this, register::class.java)
            startActivity(intent)
            finish()
        }

        btnlogin.setOnClickListener {
            val Email = email.text.toString().trim()
            val Password = password.text.toString().trim()

            // validate data
            if (Email.isEmpty() ) {
                Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (Password.isEmpty() ) {
                Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(Email, Password).addOnSuccessListener {

                // Login successful
                progressDialog.cancel()
                Toast.makeText(this, "Login successful.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            }
                .addOnFailureListener {
                    // Login failed
                    progressDialog.cancel()
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
                }
        }

        btngoole.setOnClickListener {
            beginGoogleLogin()
        }


    }

    private fun beginGoogleLogin() {
        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignInIntent)
    }

    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->

        if(result.resultCode == RESULT_OK){

            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            val account = task.getResult(ApiException::class.java)!!
            // sign in success, let's signin with firebase auth
            firebaseAuthWithGoogleAccount(account.idToken)
        }
    }

    private fun firebaseAuthWithGoogleAccount(idToken: String?) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)

        auth.signInWithCredential(credential)
            .addOnSuccessListener{
                val isNewUser = it.additionalUserInfo?.isNewUser ?: false

                if(isNewUser){
                    // new account created let's save user info to firebase realtime database
                    updateUserdb()
                }
                else{
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity((intent))
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT)
            }
    }

    private fun updateUserdb(){
        progressDialog.setMessage("Saving User Info")

        val registeredUserEmail = auth.currentUser!!.email
        val registeredUserId = auth.uid
        val name = auth.currentUser?.displayName

        val hashMap = HashMap<String, Any>()

        hashMap["name"] = "$name"
        hashMap["Phoneno"] = ""
        hashMap["profileimgurl"] = ""
        hashMap["rollno"] = ""
        hashMap["branch"] = ""
        hashMap["Course"] = ""
        hashMap["onlinestatus"] = true
        hashMap["email"] = "$registeredUserEmail"
        hashMap["uid"] = "$registeredUserId"
        hashMap["password"] = ""
        hashMap["userType"] = "Email"
        hashMap["joinedCom"] = listOf<String>()

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(registeredUserId!!)
            .setValue(hashMap)
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