package com.example.gobitecustomer.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.icu.lang.UCharacter.IndicPositionalCategory.RIGHT
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.model.MenuItemModel
import com.example.gobitecustomer.data.model.NotificationTokenUpdate
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ActivityHomeBinding
import com.example.gobitecustomer.databinding.HeaderLayoutBinding
import com.example.gobitecustomer.ui.contactus.ContactUsActivity
import com.example.gobitecustomer.ui.contributors.ContributorsActivity
import com.example.gobitecustomer.ui.login.LoginActivity
import com.example.gobitecustomer.ui.order.OrdersActivity
import com.example.gobitecustomer.ui.profile.ProfileActivity
import com.example.gobitecustomer.ui.profile.ProfileViewModel
import com.example.gobitecustomer.ui.restaurant.RestaurantActivity
import com.example.gobitecustomer.utils.AppConstants
import com.example.gobitecustomer.utils.FcmUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.Gson
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Timer

class HomeActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel:HomeViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var progressDialog: ProgressDialog
    private var shopList: ArrayList<shops> = ArrayList()
    private var cartList: ArrayList<MenuItemModel> = ArrayList()
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackbar: Snackbar
    private var placeId = ""
    var isError = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupMaterialDrawer()
        setObservers()
        placeId = preferencesHelper.getPlace()?.id.toString()
//        viewModel.getShops(placeId)
//        cartSnackBar.setAction("View Cart") { startActivity(Intent(applicationContext, CartActivity::class.java)) }
//        errorSnackbar.setAction("Try again") {
//            viewModel.getShops(preferencesHelper.getPlace()?.id.toString())
//        }
//        binding.swipeRefreshLayout.setOnRefreshListener {
//                viewModel.getShops(placeId)
//        }

//        getFCMToken()
//        FcmUtils.subscribeToTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL)
    }

    private fun getFCMToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                if(preferencesHelper.fcmToken!=token){
                    preferencesHelper.fcmToken = token
                    preferencesHelper.fcmToken?.let {
                        profileViewModel.updateFcmToken(NotificationTokenUpdate(it,preferencesHelper.userId.toString()))
                    }
                }else{
                    //FCM token is same. No need to update
                }
                val msg = "FCM TOKEN "+token
                Log.d("FCM", msg)
            })
    }

    private fun setObservers() {

        viewModel.performFetchShopsStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        isError = false
                        if(!binding.swipeRefreshLayout.isRefreshing) {
                            binding.layoutStates.visibility = View.VISIBLE
                            binding.animationView.visibility = View.GONE
                        }
                        errorSnackbar.dismiss()
                        //progressDialog.setMessage("Getting Outlets")
                        //progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("empty_animation.json")
                        binding.animationView.playAnimation()
                        //progressDialog.dismiss()
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        errorSnackbar.setText("No Outlets in this place")
                        Handler().postDelayed({errorSnackbar.show()},500)
                    }
                    Resource.Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isError = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        //progressDialog.dismiss()
                        errorSnackbar.dismiss()
                        shopList.clear()
                        it.data?.let { it1 -> shopList = it1.data.shops }
                        setupShopRecyclerView()
                        preferencesHelper.shopList = Gson().toJson(shopList)
                        updateCartUI()
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("no_internet_connection_animation.json")
                        binding.animationView.playAnimation()
                        //progressDialog.dismiss()
                        errorSnackbar.setText("No Internet Connection")
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        Handler().postDelayed({errorSnackbar.show()},500)
                    }
                    Resource.Status.ERROR -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        //progressDialog.dismiss()
                        binding.layoutStates.visibility = View.GONE
//                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("order_failed_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackbar.setText("Something went wrong")
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        Handler().postDelayed({errorSnackbar.show()},500)
                    }
                }
            }
        })

    }

    private fun setupMaterialDrawer() {

        headerLayout.layoutHeader.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }
        var identifier = 0L
        val profileItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("My Profile")
            .withIcon(R.drawable.ic_drawer_user)
        val ordersItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Your Orders")
            .withIcon(R.drawable.ic_drawer_past_rides)
        val contactUsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contact Us")
            .withIcon(R.drawable.ic_drawer_mail)
        val signOutItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Sign out")
            .withIcon(R.drawable.ic_drawer_log_out)
        val contributorsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contributors")
            .withIcon(R.drawable.ic_drawer_info)
        drawer = DrawerBuilder()
            .withActivity(this)
            .withDisplayBelowStatusBar(false)
            .withHeader(headerLayout.root)
            .withTranslucentStatusBar(true)
            .withCloseOnClick(true)
            .withSelectedItem(-1)
            .addDrawerItems(
                profileItem,
                ordersItem,
                contactUsItem,
                contributorsItem,
                DividerDrawerItem(),
                signOutItem
            )
            .withOnDrawerItemClickListener { view, position, drawerItem ->
                if (profileItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                }
                if (ordersItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, OrdersActivity::class.java))
                }
                if (contributorsItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, ContributorsActivity::class.java))
                }
                if (contactUsItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, ContactUsActivity::class.java))
                }
                if (signOutItem.identifier == drawerItem.identifier) {
                    MaterialAlertDialogBuilder(this@HomeActivity)
                        .setTitle("Confirm Sign Out")
                        .setMessage("Are you sure want to sign out?")
                        .setPositiveButton("Yes") { _, _ ->
                            FcmUtils.unsubscribeFromTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL)
                            FirebaseAuth.getInstance().signOut()
                            preferencesHelper.clearPreferences()
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()
                        }
                        .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                        .show()
                }
                true
            }.withDrawerGravity(GravityCompat.END)
            .build()

    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        headerLayout = DataBindingUtil.inflate(LayoutInflater.from(applicationContext), R.layout.header_layout, null, false)
        cartSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        cartSnackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackbar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))

        val spinner: Spinner=binding.spinner
        val items = resources.getStringArray(R.array.campus)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        binding.imageMenu.setOnClickListener(this)
        binding.textSearch.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        setStatusBarHeight()
        setupShopRecyclerView()
    }

    private fun setupShopRecyclerView() {
        shopAdapter = ShopAdapter(applicationContext, shopList, object : ShopAdapter.OnItemClickListener {
            override fun onItemClick(item: shops, position: Int) {
                val intent = Intent(applicationContext, RestaurantActivity::class.java)//todo here
                intent.putExtra(AppConstants.SHOP, Gson().toJson(item))
                startActivity(intent)
            }
        })
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = AlphaInAnimationAdapter(shopAdapter)
    }

    private fun setStatusBarHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rectangle = Rect()
                val window = window
                window.decorView.getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                val layoutParams = headerLayout.statusbarSpaceView.layoutParams
                layoutParams.height = statusBarHeight
                headerLayout.statusbarSpaceView.layoutParams = layoutParams
                Log.d("Home", "status bar height $statusBarHeight")
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }


    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.image_menu -> {
                    drawer.openDrawer()
                }
//                R.id.text_search -> {
//                    startActivity(Intent(applicationContext, SearchActivity::class.java))
//                }
            }
        }
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@HomeActivity)
            .setTitle("Exit app?")
            .setMessage("Are you sure want to exit the app?")
            .setPositiveButton("Yes") { dialog, which ->
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()
        updateGreetingMessage()
        //Checking whether user has changed their place and refreshing shops accordingly
        viewModel.getShops()
        cartList.clear()
        cartList.addAll(getCart())
        updateCartUI()
        updateHeaderLayoutUI()
        if(isError){
            errorSnackbar.show()
        }
    }

    fun getCart(): ArrayList<MenuItemModel> {
        val items: ArrayList<MenuItemModel> = ArrayList()
        val temp = preferencesHelper.getCart()
        if (!temp.isNullOrEmpty()) {
            items.addAll(temp)
        }
        return items
    }

    private fun updateGreetingMessage(){
        val timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var message = ""
        when (timeOfDay) {
            in 0..11 -> message = "hey,\n"
            in 12..15 -> message = "hey,\n"
            in 16..23 -> message = "hey,\n"
        }
        var temp = preferencesHelper.name
        if(temp==null){
            temp="praneki"
        }
        var tempList = temp?.split(" ")
        message += if(!tempList.isNullOrEmpty()){
            tempList[0]
        }else{
            preferencesHelper.name
        }
        binding.textGreeting.text = message
    }

    private fun updateCartUI() {
        var total = 0
        var totalItems = 0
        if (cartList.size > 0) {
            for (i in cartList.indices) {
                total += cartList[i].price * cartList[i].quantity
                totalItems += 1
            }
            if (totalItems == 1) {
                cartSnackBar!!.setText("₹$total | $totalItems item")
            } else {
                cartSnackBar!!.setText("₹$total | $totalItems items")
            }
            cartSnackBar!!.show()
        } else {
            cartSnackBar!!.dismiss()
        }
    }

    private fun updateHeaderLayoutUI() {
        headerLayout.textCustomerName.text = preferencesHelper.name
        headerLayout.textEmail.text = preferencesHelper.email
        val letter = preferencesHelper.name?.get(0).toString()
        val textDrawable = TextDrawable.builder()
            .buildRound(letter, ContextCompat.getColor(this, R.color.accent))
        headerLayout.imageProfilePic.setImageDrawable(textDrawable)
        //binding.imageMenu.setImageDrawable(textDrawable);
    }
}