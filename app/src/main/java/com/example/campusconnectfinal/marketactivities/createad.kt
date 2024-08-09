package com.example.campusconnectfinal.marketactivities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.campusconnectfinal.R
import com.example.campusconnectfinal.databinding.ActivityCreateadBinding
import com.example.campusconnectfinal.fragments.market
import com.example.campusconnectfinal.register
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class createad : AppCompatActivity() {

    private lateinit var binding: ActivityCreateadBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init/setup ProgressDialog to show while adding/updating the Ad
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)
        //Firebase Auth for auth related tasks
        auth = FirebaseAuth.getInstance()

        binding.backbtn.setOnClickListener {
            onBackPressed()
        }

        binding.addimgbtn.setOnClickListener{
            imagePickDialog()
        }

        binding.publishbtn.setOnClickListener {
            validatedata()
        }
    }

    private var title = ""
    private var des = ""
    private var price = ""
    private var used = ""

    private fun validatedata() {
        title = binding.titleEt.text.toString().trim()
        des = binding.desEt.text.toString().trim()
        price = binding.priceEt.text.toString().trim()
        used = binding.usedEt.text.toString().trim()

        if (title.isEmpty()) {
            //no brand entered in brandet, show error in brandet and focus
            binding.titleEt.error = "Enter Title"
            binding.titleEt.requestFocus()
        } else if (des.isEmpty()) {
            binding.desEt.error = "Enter Description"
            binding.desEt.requestFocus()
        } else if (price.isEmpty()) {
            binding.priceEt.error = "Enter Price"
            binding.priceEt.requestFocus()
        } else if(used.isEmpty()){
            binding.usedEt.error = "Enter Used Duration"
        }
        else {
            postad()
        }
    }

    private fun postad(){
        progressDialog.setMessage("Publishing Ad")
        progressDialog.show()


        val refAds = FirebaseDatabase.getInstance().getReference( "Ads")
        val keyId = refAds.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$keyId"
        hashMap["uid"] = "${auth.uid}"
        hashMap["title"] = "$title"
        hashMap["des"] = "$des"
        hashMap["price"] = "$price"
        hashMap["used"] = "$used"

        //set data to firebase database. Ads -> Adid> AdDataJSON
        refAds.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                addUserAdId(keyId)
                uploadProfileImageStorage(keyId)
            }

            .addOnFailureListener{
                progressDialog.dismiss()
                Toast.makeText((this), "Failed to post", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImageStorage(adId: String){
        //show progress

        progressDialog.show()

        val filePathAndName = "Ads/${auth.uid}_${System.currentTimeMillis()}"

        val ref = FirebaseStorage.getInstance().reference.child(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnProgressListener {

            }
            .addOnSuccessListener {
                //image uploaded successfully, get url of uploaded image
                val uriTask = it.storage.downloadUrl

                while(!uriTask.isSuccessful);
                val uploadedImageUrl = uriTask.result

                if(uriTask.isSuccessful){

                    val hashMap = HashMap<String, Any>()
                    hashMap["imgurl"] = "$uploadedImageUrl"

                    val ref = FirebaseDatabase.getInstance().getReference("Ads")
                    ref.child(adId).child("images")
                        .updateChildren(hashMap)
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Published", Toast.LENGTH_SHORT).show()
                            intent = Intent(this, createad::class.java)
                            startActivity(intent)
                            imageUri = null
                            finish()
                        }
                        .addOnFailureListener {
                            progressDialog.dismiss()
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Upload", Toast.LENGTH_SHORT).show()
            }

    }

    private fun imagePickDialog(){
        val popupMenu = PopupMenu(this, binding.addimgbtn)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")

        //show pop up menu
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {
            val itemId = it.itemId

            if(itemId == 1){
                Log.d(ContentValues.TAG, "imagePickDialog: Gallery clicked, check if storage permission granted or not")


                // Device version is TIRAMISU or above, We only need Camera
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    requestCameraPermission.launch(arrayOf(android.Manifest.permission.CAMERA))
                }

                // Device version is below TIRAMISU, We need Camera & storage permissions
                else{
                    requestCameraPermission.launch(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }

            }
            else if(itemId == 2){
                Log.d(ContentValues.TAG, "imagePickDialog: Gallery clicked, check if storage permission granted or not")

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    //device version is TIRAMISU or above , we donot need storage permission to laumch gallery
                    pickImageGallery()
                }
                else{
                    //device version is below , we need storage permission to launch gallery
                    requestStoragePermission.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }

            }

            return@setOnMenuItemClickListener true
        }

    }

    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            //lets check if permissions are granted or not

            var areAllGranted = true

            for(isGranted in it.values){
                areAllGranted = areAllGranted && isGranted
            }

            if(areAllGranted){
                pickImageCamera()
            }
            else{
                Toast.makeText((this), "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val requestStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){

            if(it){
                pickImageGallery()
            }
            else{
                Toast.makeText(this, "Storage Permission denied...", Toast.LENGTH_SHORT).show()

            }
        }

    private fun pickImageCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_image_title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_image_description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        try {
            cameraActivityResultLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error launching camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        try {
            galleryActivityResultLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error launching gallery", Toast.LENGTH_SHORT).show()
        }
    }


    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                val data = it.data
                imageUri = data!!.data

                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.baseline_person_24_white)
                        .into(binding.productimg)
                }
                catch (e: java.lang.Exception){

                }
            }
        }

    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            //check if image is captured or not

            if(it.resultCode == Activity.RESULT_OK){

                try {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.baseline_person_24_white)
                        .into(binding.productimg)
                }

                catch (e: Exception){

                }
            }
            else{
                //
            }
        }

    private fun addUserAdId(adId: String) {
        val userAdsRef = FirebaseDatabase.getInstance().getReference("Users/${auth.uid}/ads")
        userAdsRef.push().setValue(adId)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Ad ID added to user profile: $adId")
                } else {
                    Log.e(TAG, "Failed to add ad ID to user profile: $adId")
                }
            }
    }
}



