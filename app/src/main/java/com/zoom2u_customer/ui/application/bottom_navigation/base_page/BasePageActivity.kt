package com.zoom2u_customer.ui.application.bottom_navigation.base_page

import android.Manifest
import android.app.AlertDialog
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.zoom2u_customer.R
import com.zoom2u_customer.apiclient.ApiClient.Companion.getServices
import com.zoom2u_customer.apiclient.ServiceApi
import com.zoom2u_customer.databinding.ActivityBasepageBinding
import com.zoom2u_customer.getBrainTree.GetBrainTreeRepository
import com.zoom2u_customer.ui.application.bottom_navigation.profile.ProfileRepository
import com.zoom2u_customer.ui.application.chat.ChatList
import com.zoom2u_customer.ui.application.chat.ChatListRepository
import com.zoom2u_customer.ui.application.chat.LoadChatBookingArray
import com.zoom2u_customer.ui.application.get_location.GetLocationClass
import com.zoom2u_customer.ui.log_in.LoginResponse
import com.zoom2u_customer.utility.AppPreference
import com.zoom2u_customer.utility.AppUtility
import com.zoom2u_customer.utility.DialogActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class BasePageActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    private val APP_PERMISSION_REQUEST_CODE = 1010
    lateinit var viewModel: BasePageViewModel
    private var repository: BasePageRepository? = null
    private var profileRepository: ProfileRepository? = null
    lateinit var binding: ActivityBasepageBinding
    private lateinit var mainPagerAdapter: MainPagerAdapter
    private var mAuthFirebase: FirebaseAuth? = null
    private var firebaseCurrentUser: FirebaseUser? = null
    private var getLocationClass: GetLocationClass? = null
    var repositoryChat: ChatListRepository? = null
    var defaultScreen:MainScreen?=null
    var isHome:Boolean=false
    private var repositoryBrain: GetBrainTreeRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_basepage)
        viewModel = ViewModelProvider(this)[BasePageViewModel::class.java]
        val serviceApi: ServiceApi = getServices()
        repository = BasePageRepository(serviceApi, this)
        profileRepository = ProfileRepository(serviceApi, this)
        viewModel.repository = repository
        viewModel.profileRepository = profileRepository
        repositoryBrain = GetBrainTreeRepository(serviceApi,this)
        viewModel.getAccountType()
        repositoryChat = ChatListRepository(serviceApi,  this)
        repositoryChat?.getChatList(onSuccess = ::onSuccess)
        mAuthFirebase = FirebaseAuth.getInstance()
        if(mAuthFirebase!=null)
        loginInFirebase(mAuthFirebase)
        callServiceForGetBrainClientToken()
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            viewModel.sendDeviceTokenIDWithOutLocation(token)
            Log.d("Fcm", "$token")
        })


        getLocationClass = GetLocationClass(this)
        //  getLocationClass?.getCurrentLocation(onAddress = ::getAddress)
        checkToEnableAppPermissions()

        mainPagerAdapter = MainPagerAdapter(supportFragmentManager)

        mainPagerAdapter.setItems(
            arrayListOf(
                MainScreen.LOGS,
                MainScreen.PROGRESS,
                MainScreen.PROFILE,
                MainScreen.WORK
            )
        )

        val defaultScreen = MainScreen.LOGS

        scrollToScreen(defaultScreen)
        selectBottomNavigationViewMenuItem(defaultScreen.menuItemId)
        supportActionBar?.setTitle(defaultScreen.titleStringId)

        binding.navigation.setOnNavigationItemSelectedListener(this)
        binding.navigation.itemIconTintList = null;
        binding.viewPager.adapter = mainPagerAdapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val selectedScreen = mainPagerAdapter.getItems()[position]
                selectBottomNavigationViewMenuItem(selectedScreen.menuItemId)
                supportActionBar?.setTitle(selectedScreen.titleStringId)
                /**if scroll from bid to home make item unselect*/
                if(position==1){
                    val intent = Intent("home_page")
                    intent.putExtra("message", "from_bid_to_home")
                    LocalBroadcastManager.getInstance(this@BasePageActivity).sendBroadcast(intent)
                }
            }
        })
        /*if(intent.hasExtra("openHome")){
            openPage(2131362553)
        }*/

    }
    private fun onSuccess(message: String) {
        if (message != "false") {
            val arrayOfChatDelivery: MutableList<ChatList> = ArrayList()
            val mainJObjOfChatDelivery = JSONObject(message)
            if (mainJObjOfChatDelivery.getBoolean("Success")) {
                val jsonArrayOfChatDelivery = JSONArray(mainJObjOfChatDelivery.getString("data"))
                if (jsonArrayOfChatDelivery.length() > 0) {
                    LoadChatBookingArray.customerID = jsonArrayOfChatDelivery.getJSONObject(0).getString("CustomerId")
                    LoadChatBookingArray.setCourierToOfflineFromChat()
                    for (i in 0 until jsonArrayOfChatDelivery.length()) {
                        val chatList = ChatList(this, jsonArrayOfChatDelivery.getJSONObject(i))
                        arrayOfChatDelivery.add(chatList)
                    }
                    LoadChatBookingArray(arrayOfChatDelivery.reversed().toMutableList())
                }
            }
        }
    }

    private fun callServiceForGetBrainClientToken() {
        val thread = Thread {
            try {
                repositoryBrain?.getBrainTreeToken(onSuccess = ::onTokenSuccess)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun onTokenSuccess(brainToken:String) {
       // Toast.makeText(this,brainToken,Toast.LENGTH_LONG).show()
        AppPreference.getSharedPrefInstance().setBrainToken(brainToken)
    }

    private fun loginInFirebase(mAuthFirebase:FirebaseAuth?) {

        val authUserPassword = "zoom2u123456"    //******** Firebase Auth password for all courier ********//
        val loginResponse: LoginResponse? =
            AppPreference.getSharedPrefInstance().getLoginResponse()

        mAuthFirebase?.signInWithEmailAndPassword(loginResponse?.email.toString(), authUserPassword)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = mAuthFirebase.currentUser

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                   /* Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()*/
                }
            }
    }




    private fun getAddress(lat: Double, lang: Double) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                //Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            viewModel.sendDeviceTokenID(lat, lang, token)
            Log.d("Fcm", token)
        })


    }
    private fun checkToEnableAppPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            val permission = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA
            )
            if (ContextCompat.checkSelfPermission(
                    this@BasePageActivity,
                    permission[0]
                ) === PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                    this@BasePageActivity,
                    permission[1]
                ) === PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                    this@BasePageActivity,
                    permission[2]
                ) === PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                    this@BasePageActivity,
                    permission[3]
                ) === PackageManager.PERMISSION_DENIED
            ) {
                val alertDialog = AlertDialog.Builder(this@BasePageActivity)
                alertDialog.setTitle("Permission required!")
                alertDialog.setMessage(
                    "As a location based application Zoom2u performs at its optimum if your location settings are turned on.\n\nZoom2u requires access to your images to aid with the quoting of our specialty services. Photos can help drivers provide more accurate quotes for your requirements.")
                alertDialog.setPositiveButton(
                    "Okay"
                ) { dialog, _ ->
                    ActivityCompat.requestPermissions(
                        this@BasePageActivity, arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA
                        ),
                        APP_PERMISSION_REQUEST_CODE
                    )
                    dialog.dismiss()
                }
                alertDialog.show()
            } else
                getLocationClass?.getCurrentLocation(onAddress = ::getAddress)
        }
    }


    private fun scrollToScreen(mainScreen: MainScreen) {
        val screenPosition = mainPagerAdapter.getItems().indexOf(mainScreen)
        if (screenPosition != binding.viewPager.currentItem) {
            binding.viewPager.currentItem = screenPosition
        }
    }

    private fun scrollToScreenForOpenCustomPageOpen(mainScreen: MainScreen) {
        val screenPosition = mainPagerAdapter.getItems().indexOf(mainScreen)
        binding.viewPager.currentItem = screenPosition

    }

    private fun selectBottomNavigationViewMenuItem(@IdRes menuItemId: Int) {
        binding.navigation.setOnNavigationItemSelectedListener(null)
        binding.navigation.selectedItemId = menuItemId
        binding.navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        getMainScreenForMenuItem(item.itemId)?.let {
            scrollToScreen(it)
            supportActionBar?.setTitle(it.titleStringId)
            return true
        }
        return false
    }

    fun openPage(item:Int){
        getMainScreenForMenuItem(item)?.let {
            scrollToScreen(it)
            supportActionBar?.setTitle(it.titleStringId)

        }
    }

    override fun onBackPressed() {
        DialogActivity.logoutDialog(
            this,
            "Are you sure!",
            "Are you want Logout?",
            "Ok", "Cancel",
            onCancelClick = ::onCancelClick,
            onOkClick = ::onOkClick
        )
    }

    private fun onCancelClick() {

    }

    private fun onOkClick() {
        AppUtility.onLogoutCall(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }




}