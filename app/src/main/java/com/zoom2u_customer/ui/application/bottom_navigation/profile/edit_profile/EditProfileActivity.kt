package com.zoom2u_customer.ui.application.bottom_navigation.profile.edit_profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.ContextWrapper
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityEditProfileBinding
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileResponse
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class EditProfileActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityEditProfileBinding
    private var profileResponse: ProfileResponse? = null
    lateinit var viewModel: EditProfileViewModel
    private var repository: EditProfileRepository? = null
    private val GALLERY = 1
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101
    private val CAMERA = 2
    private val IMAGE_DIRECTORY = "/demonuts"
    private var profileData: ProfileResponse? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        AppUtility.hideKeyBoardClickOutside(binding.cl3, this)
        AppUtility.hideKeyboardActivityLunched(this)

        if (intent.hasExtra("profileData")) {
            profileResponse = intent.getParcelableExtra("profileData")
            setDataToView(profileResponse)

        }
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        val serviceApi: ServiceApi = com.zoom2u_customer.apiclient.ApiClient.getServices()
        repository = EditProfileRepository(serviceApi, this)
        viewModel.repository = repository


        viewModel.editProfileSuccess()?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                AppUtility.progressBarDissMiss()
                val profile: ProfileResponse = Gson().fromJson(it, ProfileResponse::class.java)
                val intent = Intent()
                intent.putExtra("UpdateProfileData", profile)
                setResult(2, intent)
                Toast.makeText(this, "Profile details updated successfully.", Toast.LENGTH_LONG)
                    .show()
                finish()

            }

        }
        viewModel.dpSuccess?.observe(this) {
            if (!TextUtils.isEmpty(it)) {
                AppUtility.progressBarDissMiss()
                profileResponse?.Photo= it
                DialogActivity.alertDialogSingleButton(this, "Well done!", "Image updated successfully.")

            }
        }
        binding.editDp.setOnClickListener(this)
        binding.saveBtn.setOnClickListener(this)
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.dp.setOnClickListener(this)

    }

    private fun setDataToView(profileResponse: ProfileResponse?) {
        this.profileData = profileResponse
        binding.fname.setText(profileResponse?.FirstName)
        binding.lname.setText(profileResponse?.LastName)
        binding.phone.setText(profileResponse?.Mobile)
        binding.company.setText(profileResponse?.Company)
        if (!TextUtils.isEmpty(profileResponse?.Photo)) {
            binding.dp.setImageBitmap(AppUtility.getBitmapFromURL(profileResponse?.Photo))
            // Picasso.get().load(profileResponse?.Photo).into(binding.dp)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.edit_dp -> {
                showPictureDialog()

            }
            R.id.back_btn -> {
                val intent = Intent()
                intent.putExtra("UpdateProfileData", profileResponse)
                setResult(2, intent)
                finish()
            }
            /*  R.id.dp -> {
                if(!TextUtils.isEmpty(profileResponse?.Photo))
                launchImageCrop(profileData?.Photo?.toUri()!!)
            }*/
            R.id.save_btn -> {
                if (checkValidation(
                        binding.fname.text.toString().trim(),
                        binding.lname.text.toString().trim(),
                        binding.phone.text.toString().trim())){
                    profileResponse?.FirstName = binding.fname.text.toString().trim()
                    profileResponse?.LastName = binding.lname.text.toString().trim()
                    profileResponse?.Company = binding.company.text.toString().trim()
                    profileResponse?.Mobile = binding.phone.text.toString().trim()
                    viewModel.setProfile(profileResponse)
                }

            }


        }
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivityForResult(intent, GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            when (requestCode) {

                GALLERY -> {
                    if (resultCode == Activity.RESULT_OK) {
                        data?.data?.let { uri ->
                            launchImageCrop(uri)
                        }
                    } else {
                        Log.e(TAG, "Image selection error: Couldn't select that image from memory.")
                    }
                }

                CAMERA -> {
                    if (data != null) {
                        if (data.extras?.get("data") != null) {
                            val thumbnail = data.extras?.get("data") as Bitmap
                            launchImageCrop(AppUtility.getImageUri(this, thumbnail))
                            saveImage(thumbnail)
                        }
                    }
                }

                CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                    val result  = CropImage.getActivityResult(data)
                    if (resultCode == Activity.RESULT_OK) {
                        val resultUri = result.uri
                        Picasso.get().load(resultUri).into(binding.dp)
                        viewModel.uploadDp(resultUri.path)
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Log.e(TAG, "Crop error: ${result.getError()}")
                    }
                }
            }

    }


    fun removeImage() {
        binding.dp.setImageDrawable(getDrawable(R.drawable.profile))
        //Picasso.with(this).load("").into(binding.dp)
        viewModel.uploadDp("")
    }

    private fun showPictureDialog() {
        val pictureDialog: AlertDialog.Builder = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
            "Select photo from gallery",
            "Capture photo from camera",
            //  "Remove profile image"
        )
        pictureDialog.setItems(pictureDialogItems,
            DialogInterface.OnClickListener { _, which ->
                when (which) {
                    0 ->
                        pickFromGallery()
                    1 ->
                        checkPermission(
                            Manifest.permission.CAMERA,
                             CAMERA_PERMISSION_CODE )
                    //2 -> removeImage()
                }
            })
        pictureDialog.show()
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@EditProfileActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@EditProfileActivity, arrayOf(permission), requestCode)
        } else {
            checkStoragePermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE )
        }
    }


    private fun checkStoragePermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@EditProfileActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@EditProfileActivity, arrayOf(permission), requestCode)
        } else {
            takePhotoFromCamera()
        }
    }
    private fun launchImageCrop(uri: Uri) {
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1920, 1920)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(this)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    private fun checkValidation(first_name: String, last_name: String, phone: String): Boolean {
        if (first_name == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your first name")
            AppUtility.validateTextField(binding.fname)
            return false
        }
        else if (last_name == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your last name")
            AppUtility.validateTextField(binding.lname)
            if(first_name != "")
                AppUtility.removeErrorBackGround(binding.fname)
            return false
        } else if (phone == "") {
            DialogActivity.alertDialogSingleButton(this, "Alert!", "Please enter your phone number")
            AppUtility.validateTextField(binding.phone)
            if(first_name != "")
                AppUtility.removeErrorBackGround(binding.fname)
            if(last_name != "")
                AppUtility.removeErrorBackGround(binding.lname)
            return false
        } else if (phone.length > 20) {
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "Please enter your valid phone number"
            )
            AppUtility.validateTextField(binding.phone)
            return false
        } else if (!phone.matches(("^[\\s0-9\\()\\-\\+]+$").toRegex())) {
            DialogActivity.alertDialogSingleButton(
                this,
                "Alert!",
                "Please enter valid phone number"
            )
            if(first_name != "")
                AppUtility.removeErrorBackGround(binding.fname)
            if(last_name != "")
                AppUtility.removeErrorBackGround(binding.lname)
            if (phone!="")
                AppUtility.removeErrorBackGround(binding.phone)
            AppUtility.validateTextField(binding.phone)
            return false
        }
        return true
    }


    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("UpdateProfileData", profileResponse)
        setResult(2, intent)
        finish()
    }




    private fun saveImage(bitmap: Bitmap){
            val cw = ContextWrapper(applicationContext)
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val directory = cw.getDir("Zoom2u", Context.MODE_PRIVATE)
            val file = File(directory, "Zoom2u_${timeStamp}" + ".jpg")
            if (!file.exists()) {

                var fos: FileOutputStream? = null
                try {
                    fos = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.flush()
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }


    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@EditProfileActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@EditProfileActivity, "Camera Permission Denied: Allow permission from app setting.", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@EditProfileActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@EditProfileActivity, "Storage Permission Denied: Allow permission from app setting.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}