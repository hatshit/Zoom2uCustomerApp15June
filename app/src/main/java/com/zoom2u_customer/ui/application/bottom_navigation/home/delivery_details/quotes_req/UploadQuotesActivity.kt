package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.quotes_req


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient.Companion.getServices
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityUploadQuotesBinding
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid.ItemHeavyFreight
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.quotes_req.quote_confirmation_page.QuoteConfirmationActivity
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UploadQuotesActivity : AppCompatActivity(), View.OnClickListener {
    private var quoteRequestBody: JSONObject? = null
    lateinit var binding: ActivityUploadQuotesBinding
    var selectProfileImgDialog: Dialog? = null
    var count = 1
    var imageClicked=0
    private val STORAGE_PERMISSION_CODE = 101
    var requestId:Int?=null
    var arrayOfImageFiles: MutableList<String> = ArrayList()
    lateinit var viewModel: UploadQuotesViewModel
    private var repository: UploadQuotesRepository? = null
    private var packagingNotes:String=""
    private var quoteId:Int?=null
    private val GALLERY = 1
    private val CAMERA = 2
    private val IMAGE_DIRECTORY = "/demonuts"
    private val CAMERA_PERMISSION_CODE = 100
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_quotes)
        AppUtility.hideKeyboardActivityLunched(this)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        AppUtility.hideKeyBoardClickOutside(binding.pickupView, this)

        val text = "<font color=#FF000000>Item will not be packaged.</font> " +
                "<font color=#fd5373>(Please note: This option will void any damage cover for this delivery. Items sent without packaging are at the customers own risk.)  </font>"
        binding.thirdPack.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)

        viewModel = ViewModelProvider(this)[UploadQuotesViewModel::class.java]
        val serviceApi: ServiceApi = getServices()
        repository = UploadQuotesRepository(serviceApi, this)
        viewModel.repository = repository

        if (intent.hasExtra("SaveDeliveryRequestReqForQuotes")) {
            quoteRequestBody= JSONObject(intent.getStringExtra("SaveDeliveryRequestReqForQuotes"))
        }


        binding.termsCon.movementMethod=LinkMovementMethod.getInstance()
        binding.termsCon.setLinkTextColor(Color.BLACK)



        viewModel.getQuoteSuccess().observe(this) {
            if (it != null) {
                if (it.isNotEmpty()) {
                    quoteId=it[it.size-1].toInt()
                    if (it.size > 1) {
                        it.removeAt(it.size-1)
                        viewModel.uploadQuotesImage(quoteId, it)
                    }else if(it.size==1){
                        AppUtility.progressBarDissMiss()
                        val intent = Intent(this, QuoteConfirmationActivity::class.java)
                        intent.putExtra("QuoteId",quoteId.toString())
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
            }
        }

        viewModel.finalSuccess.observe(this) {
            if (it != null) {
                AppUtility.progressBarDissMiss()
                val intent = Intent(this, QuoteConfirmationActivity::class.java)
                intent.putExtra("QuoteId",it)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }
        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.uploadImage.setOnClickListener(this)
        binding.submitQuotesReq.setOnClickListener(this)
        binding.imv1.setOnClickListener(this)
        binding.imv1.isEnabled=false
        binding.imv2.isEnabled=false
        binding.imv3.isEnabled=false
        binding.imv4.isEnabled=false
        binding.imv5.isEnabled=false
        binding.imv2.setOnClickListener(this)
        binding.imv3.setOnClickListener(this)
        binding.imv4.setOnClickListener(this)
        binding.imv5.setOnClickListener(this)



        binding.packagingGroup.setOnCheckedChangeListener { _, checkedId ->
            packagingNotes = when {
                R.id.first_pack == checkedId -> " Item will be in original packaging"
                R.id.sec_pack == checkedId -> "Item will be packaged"
                else -> "Item will not be packaged"
            }
        }



    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.upload_image -> {
                showPictureDialog()
            }
            R.id.submit_quotes_req -> {
                if (checkValidation(binding.notes.text.toString(), packagingNotes)) {

                    DialogActivity.logoutDialog(
                        this,
                        "Please note!",
                        "Due to the nature of this delivery, it will be created as a quote request and sent out to drivers for bidding on, instead of being a fixed quote. Our drivers will start providing quotes for this delivery, which you can view and accept. Do you wish to continue?",
                        "Yes","No",
                        onCancelClick=::onNoClick,
                        onOkClick = ::onYesClick
                    )

                }
            }
            R.id.imv1 -> {
                showPictureDialog()
                imageClicked=1
            }
            R.id.imv2 -> {
                showPictureDialog()
                imageClicked=2
            }
            R.id.imv3 -> {
                showPictureDialog()
                imageClicked=3
            }
            R.id.imv4 -> {
                showPictureDialog()
                imageClicked=4
            }
            R.id.imv5 -> {
                showPictureDialog()
                imageClicked=5
            }
            R.id.back_btn -> {
                val intent = Intent()
                setResult(3, intent)
                finish()
            }
        }
    }

    private fun onNoClick(){

    }
    private fun onYesClick(){
        quoteRequestBody?.getJSONObject("_requestModel")?.put("isPayByInvoiceMarked",  false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("isDrivable",false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("VehicleBrand","")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("VehicleModel", "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("TrailerType",  "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("SecurityIdCardNumber", "")

        quoteRequestBody?.getJSONObject("_requestModel")?.put("OrderNumber", binding.purchaseOrder.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Notes", binding.notes.text?.trim().toString().trim())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsVehicleWithBelongings",false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsSuggestedPrice",false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsRentContainer", false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsEmptyVehicle", false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsDropIdCheckRequired",  false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("FreightTitle", "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("FreightValue", "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Distance",  "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("BookingFee", 0)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("DeliverySpeed", "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("DropIdentityNumber",  "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("OtherDetails", "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("PackagingNotes",   "Item will be in original packaging")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Price", 0)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Items", JSONArray(Gson().toJson(getItemList()).toString()))


        quoteRequestBody?.getJSONObject("_requestModel")?.put("isPayByInvoiceMarked",  false)


        viewModel.getQuoteRequest(quoteRequestBody, arrayOfImageFiles)
    }
    private fun getItemList(): MutableList<ItemHeavyFreight> {
        val itemList: MutableList<ItemHeavyFreight> = java.util.ArrayList()
        val item = ItemHeavyFreight(
            "", "", "", "",
            "","","","")
        itemList.add(0, item)

        return itemList
    }

    private fun checkValidation(desc: String, packaging_Type: String): Boolean {
        if (desc == "") {
            DialogActivity.alertDialogSingleButton(this, "Awaiting!", "Please enter your parcel description.")
            binding.notes.setBackgroundResource(R.drawable.black_box_white)
            AppUtility.validateEditTextField(
                binding.notes,
                "Notes is required."
            )
            return false
        }
       else if (packaging_Type.isNullOrEmpty()) {
           binding.packaging.setTextColor(Color.RED)
           DialogActivity.alertDialogSingleButton(this, "Awaiting!", "Please select a packaging type.")
            return false
        }
        else if(!binding.chkTerms.isChecked){
            Toast.makeText(this,"Please Accept the customer Terms and Conditions.", Toast.LENGTH_LONG).show()
            return false
        }
     return true
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
                    1 -> checkPermission(
                        Manifest.permission.CAMERA,
                        CAMERA_PERMISSION_CODE)
                    //2 -> removeImage()
                }
            })
        pictureDialog.show()
    }
    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@UploadQuotesActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@UploadQuotesActivity, arrayOf(permission), requestCode)
        } else {
            checkStoragePermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE )

        }
    }

    private fun checkStoragePermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@UploadQuotesActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@UploadQuotesActivity, arrayOf(permission), requestCode)
        } else {
            takePhotoFromCamera()
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
    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .start(this)
    }
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    fun saveImage(bitmap: Bitmap){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GALLERY -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        launchImageCrop(uri)
                    }
                }
                else{
                    Log.e(ContentValues.TAG, "Image selection error: Couldn't select that image from memory." )
                }
            }

            CAMERA ->{
                if(data!=null) {
                    if (data.extras?.get("data") != null) {
                        val thumbnail = data.extras?.get("data") as Bitmap
                        launchImageCrop(AppUtility.getImageUri(this, thumbnail))
                        saveImage(thumbnail)
                    }
                }
            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == RESULT_OK) {
                    val resultUri = result.uri
                    if (imageClicked == 0) {
                        when (count) {
                            1 -> {
                                Picasso.get().load(resultUri).into(binding.imv1)
                                binding.imv1.isEnabled = true
                                count++
                                arrayOfImageFiles.add( resultUri.path.toString())
                            }
                            2 -> {
                                Picasso.get().load(resultUri).into(binding.imv2)
                                count++
                                binding.imv2.isEnabled = true
                                arrayOfImageFiles.add( resultUri.path.toString())
                            }
                            3 -> {
                                Picasso.get().load(resultUri).into(binding.imv3)
                                count++
                                binding.imv3.isEnabled = true
                                arrayOfImageFiles.add( resultUri.path.toString())
                            }
                            4 -> {
                                Picasso.get().load(resultUri).into(binding.imv4)
                                count++
                                binding.imv4.isEnabled = true
                                arrayOfImageFiles.add( resultUri.path.toString())
                            }
                            5 -> {
                                Picasso.get().load(resultUri).into(binding.imv5)
                                count++
                                binding.imv5.isEnabled = true
                                arrayOfImageFiles.add( resultUri.path.toString())
                                binding.uploadImage.isClickable = false
                            }
                        }
                    } else {
                        when (imageClicked) {
                            1 -> {
                                Picasso.get().load(resultUri).into(binding.imv1)
                                arrayOfImageFiles[0] = resultUri.path.toString()
                                imageClicked=0
                            }
                            2 -> {
                                Picasso.get().load(resultUri).into(binding.imv2)
                                arrayOfImageFiles[1] = resultUri.path.toString()
                                imageClicked=0
                            }
                            3 -> {
                                Picasso.get().load(resultUri).into(binding.imv3)
                                arrayOfImageFiles[2] = resultUri.path.toString()
                                imageClicked=0
                            }
                            4 -> {
                                Picasso.get().load(resultUri).into(binding.imv4)
                                arrayOfImageFiles[3] = resultUri.path.toString()
                                imageClicked=0
                            }
                            5 -> {
                                Picasso.get().load(resultUri).into(binding.imv5)
                                arrayOfImageFiles[4] = resultUri.path.toString()
                                imageClicked=0
                            }

                        }
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@UploadQuotesActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@UploadQuotesActivity, "Camera Permission Denied: Allow permission from app setting.", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@UploadQuotesActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@UploadQuotesActivity, "Storage Permission Denied: Allow permission from app setting.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onBackPressed() {
        val intent = Intent()
        setResult(3, intent)
        finish()
    }

}

