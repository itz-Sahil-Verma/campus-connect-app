package com.example.campusconnectfinal.profileactivities

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
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
import com.example.campusconnectfinal.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class edit_profile : AppCompatActivity() {
    private lateinit var binding : ActivityEditProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var progressDialog : ProgressDialog
    private var myUserType = ""
    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait....")
        progressDialog.setCanceledOnTouchOutside(false)

        auth = FirebaseAuth.getInstance()
        loadmyinfo()

        // handle toolbar backbtn click, go back
        binding.toolbarbackbtn.setOnClickListener {
            onBackPressed()
        }

        binding.profileimagePickFab.setOnClickListener {
            imagePickDialog()
        }

        binding.updatebtn.setOnClickListener {
            validateData()
        }

    }

    private var name = ""
    private var Rollno = ""
    private var course = ""
    private var branch = ""
    private var phonecode = ""
    private var phonenum = ""

    private fun validateData(){

        name = binding.nameEt.text.toString().trim()
        Rollno = binding.rollEt.text.toString().trim()
        course = binding.courseEt.text.toString().trim()
        branch = binding.branchEt.text.toString().trim()
        phonecode = binding.CoountryCodePicker.selectedCountryCodeWithPlus
        phonenum = binding.phoneEt.text.toString().trim()


        if(imageUri == null){
            updateProfileDb(null)
        }

        else{
            uploadProfileImageStorage()
        }
    }

    private fun uploadProfileImageStorage(){
        //show progress

        progressDialog.setMessage("Uploading user profile image")
        progressDialog.show()

        val filePathAndName = "Users/${auth.uid}_${System.currentTimeMillis()}"

        val ref = FirebaseStorage.getInstance().reference.child(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnProgressListener {
                //check image upload progress and show
                val progress = 100.0* it.bytesTransferred/ it.totalByteCount

                progressDialog.setMessage("Uploading profile image. Progress: $progress")
            }.continueWithTask { task ->
                // Chain a task to get the download URL
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                ref.downloadUrl
            }

            .addOnSuccessListener {
                //image uploaded successfully, get url of uploaded image
                // The download URL is available here
                val uploadedImageUrl = it.toString()
                updateProfileDb(uploadedImageUrl)
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to Upload", Toast.LENGTH_SHORT).show()
            }

    }

    private fun updateProfileDb(uploadedImageUrl: String?){

        progressDialog.setMessage("Updating user info")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["name"] = "$name"
        hashMap["branch"] = "$branch"
        hashMap["course"] = "$course"
        hashMap["rollno"] = "$Rollno"

        if(uploadedImageUrl != null){
            //update profile image in db only if uploaded image url is not null
            hashMap["profileimageurl"] = "$uploadedImageUrl"
        }
        else{

        }

        hashMap["phonecode"] = "$phonecode"
        hashMap["Phoneno"] = "$phonenum"

        //database reference of user to update info

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${auth.uid}")
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "Updated...", Toast.LENGTH_SHORT).show()

                imageUri = null
            }
            .addOnFailureListener {e ->
                progressDialog.dismiss()
                Log.e(ContentValues.TAG, "Failed to update profile", e)
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
            }

    }
    private fun loadmyinfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${auth.uid}")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val phonenum = "${snapshot.child("phoneNumber").value}"
                    val branch = "${snapshot.child("branch").value}"
                    val rollno = "${snapshot.child("rollNo").value}"
                    val course = "${snapshot.child("course").value}"
                    val profileimgurl = "${snapshot.child("profileimgurl").value}"
                    myUserType = "${snapshot.child("usertype").value}"
                    val phoneCode = "${snapshot.child("PhoneCode").value}"


                    val phone = phoneCode + phonenum

                    //set data to ui
                    binding.nameEt.setText(name)
                    binding.phoneEt.setText(phonenum)
                    binding.branchEt.setText(branch)
                    binding.rollEt.setText(rollno)
                    binding.courseEt.setText(course)

                    try{
                        val phoneCodeInt = phoneCode.replace("+","").toInt()  //eg +92-> 92
                        binding.CoountryCodePicker.setCountryForPhoneCode(phoneCodeInt)
                    }
                    catch (e: Exception){
                        Log.e(ContentValues.TAG, "onDataChange: ", e)
                    }

                    try {
                        Glide.with(this@edit_profile)
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

    private fun imagePickDialog(){
        val popupMenu = PopupMenu(this, binding.profileimagePickFab)

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
                        .into(binding.profileIv)
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
                        .into(binding.profileIv)
                }

                catch (e: Exception){

                }
            }
            else{
                //
            }
        }

}