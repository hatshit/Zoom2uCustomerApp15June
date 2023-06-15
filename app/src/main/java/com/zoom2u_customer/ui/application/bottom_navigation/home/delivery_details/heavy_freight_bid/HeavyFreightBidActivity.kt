package com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.heavy_freight_bid

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityHeavyFreightBidBinding
import com.zoom2u_customer.ui.application.bottom_navigation.bid_request.active_bid_request.ActiveBidListResponse
import com.zoom2u_customer.ui.application.bottom_navigation.home.availabilty_delievry.suggest_price.Item
import com.zoom2u_customer.ui.application.bottom_navigation.home.delivery_details.quotes_req.quote_confirmation_page.QuoteConfirmationActivity
import com.zoom2u_customer.ui.application.bottom_navigation.home.home_fragment.Icon
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

class HeavyFreightBidActivity : AppCompatActivity() , View.OnClickListener{
    lateinit var binding: ActivityHeavyFreightBidBinding
    var adapter: HeavyFreightItemAdapter? = null
    private var dataList: ArrayList<Item> = ArrayList()
    private var largeItem: Icon? = null
    private var quoteRequestBody: JSONObject? = null
    var selectProfileImgDialog: Dialog? = null
    var count = 1
    var imageClicked=0
    private val STORAGE_PERMISSION_CODE = 101
    var requestId:Int?=null
    var arrayOfImageFiles: MutableList<String> = ArrayList()
    lateinit var viewModel: HeavyFreightViewModel
    private var repository: HeavyFreightRepository? = null
    private var quoteId:Int?=null
    private var heavyFreightQuoteRef:String?=null
    private val GALLERY = 1
    private val CAMERA = 2
    private val CAMERA_PERMISSION_CODE = 100
    private var isVehicleBelonging: Boolean= false
    private var isEmptyVehicle: Boolean =true
    private var itemCategory:String?=null
    private var itemCategory1:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_heavy_freight_bid)
        AppUtility.hideKeyboardActivityLunched(this)
        AppUtility.hideKeyBoardClickOutside(binding.parentCl, this)
        AppUtility.hideKeyBoardClickOutside(binding.pickupView, this)

        if (intent.hasExtra("LargeItem")) {
            largeItem = intent.getParcelableExtra<Icon>("LargeItem")
            quoteRequestBody = JSONObject(intent.getStringExtra("SaveDeliveryRequestReqForQuotes"))
            itemCategory=quoteRequestBody?.getJSONObject("_requestModel")?.get("FreightCategory").toString()


            when (largeItem?.Value) {
                22 -> {
                    /**for Vehicles type*/
                    binding.vehicleLl.visibility=View.VISIBLE
                }
                23 -> {
                    /**for Container type*/
                    binding.containerLl.visibility=View.VISIBLE
                }
                24 -> {
                   /**for truck type*/
                    binding.card2.visibility=View.VISIBLE
                    binding.freightTitle.visibility=View.GONE
                    binding.freightTitleTxt.visibility=View.GONE
                    binding.freightValue.visibility=View.GONE
                    binding.freightValueTxt.visibility=View.GONE
                    binding.deliveryInstructions.visibility=View.GONE
                    binding.deliveryInstructionsTxt.visibility=View.GONE
                }
            }

        }
        setAdapterView()

        viewModel = ViewModelProvider(this)[HeavyFreightViewModel::class.java]
        val serviceApi: ServiceApi = ApiClient.getServices()
        repository = HeavyFreightRepository(serviceApi, this)
        viewModel.repository = repository


        binding.termsCon.movementMethod= LinkMovementMethod.getInstance()
        binding.termsCon.setLinkTextColor(Color.BLACK)


        viewModel.getQuoteSuccess().observe(this) {

            if (it != null) {
                if (it.isNotEmpty()) {
                    itemCategory1 = it[it.size-1]
                    quoteId=it[it.size-3].toInt()
                    heavyFreightQuoteRef= it[it.size-2]
                    if (it.size > 3) {
                        it.removeAt(it.size-3)
                        it.removeAt(it.size-2)
                        it.removeAt(it.size-1)
                        viewModel.uploadQuotesImage(quoteId,heavyFreightQuoteRef,itemCategory,it)
                    }else if(it.size==3){
                        AppUtility.progressBarDissMiss()
                        val activeBidItem = ActiveBidListResponse()
                        activeBidItem.ItemType ="Freight"
                        activeBidItem.ItemCategory =itemCategory1
                        val intent = Intent(this, QuoteConfirmationActivity::class.java)
                        intent.putExtra("HeavyFreightQuoteRef",heavyFreightQuoteRef)
                        intent.putExtra("ActiveBidItemFromHeavyFreight",activeBidItem)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                    }
                }
            }
        }

        viewModel.finalSuccess.observe(this) {
            if (it != null) {
                val activeBidItem = ActiveBidListResponse()
                activeBidItem.ItemType ="Freight"
                activeBidItem.ItemCategory = it.split("?")[1]
                AppUtility.progressBarDissMiss()
                val intent = Intent(this, QuoteConfirmationActivity::class.java)
                intent.putExtra("HeavyFreightQuoteRef", it.split("?")[0])
                intent.putExtra("ActiveBidItemFromHeavyFreight",activeBidItem)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        showErrorOnTextChange()
        binding.vehicleBelonging.setOnCheckedChangeListener { _, checkedId ->
          if (R.id.empty_vehicle == checkedId){
              isEmptyVehicle=true
              isVehicleBelonging=false
          }
          else{
              isEmptyVehicle=false
              isVehicleBelonging=true
          }
        }

        binding.zoom2uHeader.backBtn.setOnClickListener(this)
        binding.uploadImage.setOnClickListener(this)
        binding.submitQuotesReq.setOnClickListener(this)
        binding.addItemBtn.setOnClickListener(this)
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
    }

    private fun showErrorOnTextChange() {
        binding.freightTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "" ) {
                    binding.freightTitleTxt.setTextColor(Color.RED)
                    binding.freightTitle.setBackgroundResource(R.drawable.black_box_white)
                    AppUtility.validateEditTextField(binding.freightTitle, "The freight title is required*")
                }
                else if (s.toString() != "") {
                    binding.freightTitle.setBackgroundResource(R.drawable.text_background1)
                    binding.freightTitleTxt.setTextColor(Color.BLACK)
                }
            }
        })

        binding.freightValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "" ) {
                    binding.freightValueTxt.setTextColor(Color.RED)
                    binding.freightValue.setBackgroundResource(R.drawable.black_box_white)
                    AppUtility.validateEditTextField(binding.freightValue, "The freight value is required.")
                }
                else if (s.toString() != "") {
                    binding.freightValue.setBackgroundResource(R.drawable.text_background1)
                    binding.freightValueTxt.setTextColor(Color.BLACK)
                }
            }
        })

        binding.describeShipment.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "" ) {
                    binding.describeShipmentTxt.setTextColor(Color.RED)
                    binding.describeShipment.setBackgroundResource(R.drawable.black_box_white)
                    AppUtility.validateEditTextField(binding.describeShipment, "Notes is required.")
                }
                else if (s.toString() != "") {
                    binding.describeShipment.setBackgroundResource(R.drawable.text_background1)
                    binding.describeShipmentTxt.setTextColor(Color.BLACK)
                }
            }
        })

        binding.vehicleBrand.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "" ) {
                    binding.vehicleBrandTxt.setTextColor(Color.RED)
                    binding.vehicleBrand.setBackgroundResource(R.drawable.black_box_white)
                    AppUtility.validateEditTextField(binding.vehicleBrand, "The vehicle brand name is required.")
                }
                else if (s.toString() != "") {
                    binding.vehicleBrand.setBackgroundResource(R.drawable.text_background1)
                    binding.vehicleBrandTxt.setTextColor(Color.BLACK)
                }
            }
        })

        binding.vehicleModel.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() == "" ) {
                    binding.vehicleModelTxt.setTextColor(Color.RED)
                    binding.vehicleModel.setBackgroundResource(R.drawable.black_box_white)
                    AppUtility.validateEditTextField(binding.vehicleModel, "The vehicle model name is required")
                }
                else if (s.toString() != "") {
                    binding.vehicleModel.setBackgroundResource(R.drawable.text_background1)
                    binding.vehicleModelTxt.setTextColor(Color.BLACK)
                }
            }
        })
    }

    fun setAdapterView() {
        val item=Item("",-2,-2,-2,-2,-2.0,"-2","")
        dataList.add(item)
        val layoutManager = GridLayoutManager(this, 1)
        binding.itemView.layoutManager = layoutManager
        adapter = HeavyFreightItemAdapter(this, dataList)
        binding.itemView.adapter = adapter

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.add_item_btn -> {
               adapter?.addItemBtn()
            }
            R.id.upload_image -> {
                showPictureDialog()
            }
            R.id.submit_quotes_req -> {
                if(largeItem?.Value==22) {
                    if (checkValidationForVehicle(
                            binding.freightTitle.text.toString(),
                            binding.freightValue.text.toString(),
                            binding.describeShipment.text.toString(),
                            binding.vehicleBrand.text.toString(),
                            binding.vehicleModel.text.toString()
                        )
                    ) {
                        DialogActivity.logoutDialog(
                            this,
                            "Confirm!",
                            "Due to the nature of this delivery, it will be created as a quote request and sent out to drivers for bidding on, instead of being a fixed quote. Our drivers will start providing quotes for this delivery, which you can view and accept. Do you wish to continue?",
                            "Yes", "No",
                            onCancelClick = ::onNoClick,
                            onOkClick = ::onYesClick
                        )
                    }
                }
                else if(largeItem?.Value==24){
                   if(checkValidationTruck(binding.describeShipment.text.toString()))
                   {
                       DialogActivity.logoutDialog(
                           this,
                           "Confirm!",
                           "Due to the nature of this delivery, it will be created as a quote request and sent out to drivers for bidding on, instead of being a fixed quote. Our drivers will start providing quotes for this delivery, which you can view and accept. Do you wish to continue?",
                           "Yes", "No",
                           onCancelClick = ::onNoClick,
                           onOkClick = ::onYesClick
                       )
                   }

                }
                else {
                        if (checkValidation(
                                binding.freightTitle.text.toString(),
                                binding.freightValue.text.toString(),
                                binding.describeShipment.text.toString()
                            )
                        ) {
                            DialogActivity.logoutDialog(
                                this,
                                "Confirm!",
                                "Due to the nature of this delivery, it will be created as a quote request and sent out to drivers for bidding on, instead of being a fixed quote. Our drivers will start providing quotes for this delivery, which you can view and accept. Do you wish to continue?",
                                "Yes", "No",
                                onCancelClick = ::onNoClick,
                                onOkClick = ::onYesClick
                            )
                        }
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

    private fun checkValidationTruck(note: String) : Boolean {
        if (!checkAllFieldAreFilled() ||  note=="" ) {
            Toast.makeText(this,"Please fill out all mandatory fields marked in red.", Toast.LENGTH_LONG).show()
            if(note==""){
                binding.describeShipmentTxt.setTextColor(Color.RED)
                binding.describeShipment.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.describeShipment, "Notes is required.")
            }
            return false
        }

        else if(!binding.chkTerms.isChecked){
            Toast.makeText(this,"Please Accept the customer Terms and Conditions.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun checkValidationForVehicle(
        freightTitle: String, freightValue : String, note: String,
        vehicleBrand: String,
        vehicleModel: String
    ): Boolean {
        if (!checkAllFieldAreFilled() || freightTitle == ""  || freightValue=="" || note=="" ||vehicleBrand=="" || vehicleModel=="") {
            Toast.makeText(this,"Please fill out all mandatory fields marked in red.", Toast.LENGTH_LONG).show()
            if(freightTitle==""){
                binding.freightTitleTxt.setTextColor(Color.RED)
                binding.freightTitle.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.freightTitle, "The freight title is required*")
            }
            if(freightValue==""){
                binding.freightValueTxt.setTextColor(Color.RED)
                binding.freightValue.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.freightValue, "The freight value is required.")
            }
            if(note==""){
                binding.describeShipmentTxt.setTextColor(Color.RED)
                binding.describeShipment.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.describeShipment, "Notes is required.")
            }
            if(vehicleModel==""){
                binding.vehicleModelTxt.setTextColor(Color.RED)
                binding.vehicleModel.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.vehicleModel, "The vehicle model name is required")
            }
            if(vehicleBrand==""){
                binding.vehicleBrandTxt.setTextColor(Color.RED)
                binding.vehicleBrand.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.vehicleBrand, "The vehicle brand name is required.")
            }

            return false
        }

        else if(!binding.chkTerms.isChecked){
            Toast.makeText(this,"Please Accept the customer Terms and Conditions.", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun checkAllFieldAreFilled():Boolean{
        checkAllFieldAreFilled1()
        for (item in dataList) {
             if(item.Quantity==-1 || item.ItemWeightKg==-1.0 || item.LengthCm==-1 || item.HeightCm==-1 || item.WidthCm==-1){
                 return false
            }
        }
        return true
    }

    private fun checkAllFieldAreFilled1() {
        for (item in dataList) {
            if(item.Quantity==-2) item.Quantity=-1
            if(item.ItemWeightKg==-2.0) item.ItemWeightKg=-1.0
            if(item.LengthCm==-2)  item.LengthCm=-1
            if( item.HeightCm==-2) item.HeightCm=-1
            if( item.WidthCm==-2)  item.WidthCm=-1
            if(item.TotalWeightKg=="-2") item.TotalWeightKg="0"
        }
       adapter?.notifyDataSetChanged()
    }

    private fun onNoClick(){

    }
    private fun onYesClick(){
        quoteRequestBody?.getJSONObject("_requestModel")?.put("FreightTitle", binding.freightTitle.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("FreightValue", binding.freightValue.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Notes", binding.describeShipment.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("OtherDetails", binding.deliveryInstructions.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("VehicleBrand", binding.vehicleBrand.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("VehicleModel", binding.vehicleModel.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("isDrivable", binding.vehicleDrivelCheck.isChecked)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsVehicleWithBelongings", isVehicleBelonging)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsSuggestedPrice",false)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsRentContainer", binding.yesRentContainer.isChecked)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsEmptyVehicle", isEmptyVehicle)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("SecurityIdCardNumber", binding.maritimeSecurityId.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("OrderNumber", binding.purchaseOrder.text?.trim().toString())
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Items", JSONArray(Gson().toJson(getItemList()).toString()))

        quoteRequestBody?.getJSONObject("_requestModel")?.put("BookingFee", 0)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("DeliverySpeed", "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Distance",  "7.2 km")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("DropIdentityNumber",  "")


        quoteRequestBody?.getJSONObject("_requestModel")?.put("IsDropIdCheckRequired",  false)

        quoteRequestBody?.getJSONObject("_requestModel")?.put("LoadType",  "FullLoad")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("PackagingNotes",  "")

        quoteRequestBody?.getJSONObject("_requestModel")?.put("Price",  0)
        quoteRequestBody?.getJSONObject("_requestModel")?.put("TrailerType",  "")
        quoteRequestBody?.getJSONObject("_requestModel")?.put("Vehicle",  "Van")

        quoteRequestBody?.getJSONObject("_requestModel")?.put("isPayByInvoiceMarked",  false)




        viewModel.getQuoteRequest(quoteRequestBody, arrayOfImageFiles,itemCategory)
    }

    private fun getItemList(): MutableList<ItemHeavyFreight> {
        var item: ItemHeavyFreight
        val itemList: MutableList<ItemHeavyFreight> = java.util.ArrayList()
        for ((i, item1) in dataList.withIndex()) {

            item = ItemHeavyFreight(
                item1.Packaging.toString(),
                item1.Quantity.toString(), item1.LengthCm.toString(), item1.HeightCm.toString(),
                item1.WidthCm.toString(), item1.ItemWeightKg.toString(),(item1.TotalWeightKg).toString(),item1.ContainerSize
            )
            itemList.add(i, item)
        }
        return itemList
    }
    private fun checkValidation(freightTitle: String, freightValue : String, note: String): Boolean {
        if (!checkAllFieldAreFilled() || freightTitle == ""  || freightValue=="" || note=="") {
            Toast.makeText(this,"Please fill out all mandatory fields marked in red.", Toast.LENGTH_LONG).show()
            if(freightTitle==""){
                binding.freightTitleTxt.setTextColor(Color.RED)
                binding.freightTitle.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.freightTitle, "The freight title is required*")
            }
            if(freightValue==""){
                binding.freightValueTxt.setTextColor(Color.RED)
                binding.freightValue.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.freightValue, "The freight value is required.")
            }
            if(note==""){
                binding.describeShipmentTxt.setTextColor(Color.RED)
                binding.describeShipment.setBackgroundResource(R.drawable.black_box_white)
                AppUtility.validateEditTextField(binding.describeShipment, "Notes is required.")
            }
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
        if (ContextCompat.checkSelfPermission(this@HeavyFreightBidActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@HeavyFreightBidActivity, arrayOf(permission), requestCode)
        } else {
            checkStoragePermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE )

        }
    }
    private fun checkStoragePermission(
        permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@HeavyFreightBidActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@HeavyFreightBidActivity, arrayOf(permission), requestCode)
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
                Toast.makeText(this@HeavyFreightBidActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@HeavyFreightBidActivity, "Camera Permission Denied: Allow permission from app setting.", Toast.LENGTH_SHORT).show()
            }
        }else if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@HeavyFreightBidActivity, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@HeavyFreightBidActivity, "Storage Permission Denied: Allow permission from app setting.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onBackPressed() {
        val intent = Intent()
        setResult(3, intent)
        finish()
    }
}