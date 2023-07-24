package com.example.gobitecustomer.ui.home

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log.e
import android.widget.Toast
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.Item
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ActivityHomeBinding
import com.example.gobitecustomer.databinding.HeaderLayoutBinding
import com.example.gobitecustomer.ui.cart.CartActivity
import com.example.gobitecustomer.ui.contactus.ContactUsActivity
import com.example.gobitecustomer.ui.login.LoginActivity
import com.example.gobitecustomer.ui.order.OrderViewModel
import com.example.gobitecustomer.ui.order.OrdersActivity
import com.example.gobitecustomer.ui.profile.ProfileActivity
import com.example.gobitecustomer.ui.profile.ProfileViewModel
import com.example.gobitecustomer.ui.restaurant.RestaurantActivity
import com.example.gobitecustomer.ui.search.SearchActivity
import com.example.gobitecustomer.utils.AppConstants
import com.example.gobitecustomer.utils.FcmUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Calendar
import java.util.Locale

class HomeActivity: AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel:HomeViewModel by viewModel()

    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var shopAdapter: ShopAdapter
    private lateinit var progressDialog: ProgressDialog
    private var shopList: ArrayList<shops> = ArrayList()
    private var cartList: ArrayList<Item> = ArrayList()
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackbar: Snackbar
    private var placeId = ""
    var isError = false
    private lateinit var locationManager: LocationManager
    private lateinit var geocoder: Geocoder
    private lateinit var locationListener: LocationListener
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private val LOCATION_SETTINGS_REQUEST_CODE = 2
    private var isKanpur=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupMaterialDrawer()
        setObservers()


//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        geocoder = Geocoder(this, Locale.getDefault())
//
//        locationListener = object : LocationListener {
//            override fun onLocationChanged(location: Location) {
//                getCurrentLocationAddress(location)
//                locationManager.removeUpdates(this)
//            }
//            override fun onProviderDisabled(provider: String) {}
//            override fun onProviderEnabled(provider: String) {
//                getCurrentLocation()
//            }
//            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
//        }

//        getCurrentLocation()

        viewModel.change(0)
        viewModel.getShops()

        cartSnackBar.setAction("View Cart") {
            startActivity(Intent(applicationContext, CartActivity::class.java))
        }
        errorSnackbar.setAction("Try again") {
             viewModel.getShops()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
               viewModel.getShops()
        }

    }

//    private fun getCurrentLocation() {
//        if (isLocationPermissionGranted()) {
//            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
//                val providers = locationManager.getProviders(true)
//                for (provider in providers) {
//                    if (ActivityCompat.checkSelfPermission(
//                            this,
//                            Manifest.permission.ACCESS_FINE_LOCATION
//                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                            this,
//                            Manifest.permission.ACCESS_COARSE_LOCATION
//                        ) != PackageManager.PERMISSION_GRANTED
//                    ) {
//                        Toast.makeText(this, "please grant location permissions in your setting", Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                    locationManager.requestLocationUpdates(provider, 1000, 0f, locationListener)
//                }
//            }else{
//                val alertDialogBuilder = AlertDialog.Builder(this)
//                alertDialogBuilder.setTitle("Location Disabled")
//
//                val redColorSpan = ForegroundColorSpan(Color.RED)
//                val ok = SpannableString("OK")
//                ok.setSpan(redColorSpan, 0, ok.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//                alertDialogBuilder.setMessage("Please enable location to continue")
//                alertDialogBuilder.setPositiveButton(ok) { dialog: DialogInterface, _: Int ->
//                    dialog.dismiss()
//                    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                    startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE)
//                }
//                alertDialogBuilder.setCancelable(false)
//                alertDialogBuilder.show()
//            }
//
//        } else {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
//    }
//
//    private fun getCurrentLocationAddress(location: Location) {
//        try {
//            val addresses: List<Address> =
//                geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
//            if (addresses.isNotEmpty()) {
//                val address: Address = addresses[0]
//                val fullAddress = address.getAddressLine(0)
//                e("Current Location ", fullAddress)
//                if(fullAddress.contains("Kanpur") || fullAddress.contains("kanpur") || fullAddress.contains("KANPUR") || fullAddress.contains("Jaipur") ){
//                    //patna jaipur for testing only
//                    isKanpur=true
//                    viewModel.change(0)
//                    viewModel.getShops()
//                }else {
//                    isKanpur = false
//
//                    isError = true
//                    binding.swipeRefreshLayout.isRefreshing = false
//                    binding.layoutStates.visibility = View.GONE
//                    binding.animationView.visibility = View.VISIBLE
//                    binding.animationView.loop(true)
//                    binding.animationView.setAnimation("empty_animation.json")
//                    binding.animationView.playAnimation()
//                    progressDialog.dismiss()
//                    shopList.clear()
//                    shopAdapter.notifyDataSetChanged()
//                    errorSnackbar.setText("No outlets in your area")
//                    Handler().postDelayed({ errorSnackbar.show() }, 500)
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    private fun isLocationPermissionGranted(): Boolean {
//        return ContextCompat.checkSelfPermission(
//            this,
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(
//                    this,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ) == PackageManager.PERMISSION_GRANTED
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation()
//            } else {
//                println("Location permission denied")
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
//            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                getCurrentLocation()
//            } else {
//                println("Location services are disabled")
//                val alertDialogBuilder = AlertDialog.Builder(this)
//                alertDialogBuilder.setTitle("Location Disabled")
//
//                val redColorSpan = ForegroundColorSpan(Color.RED)
//                val ok = SpannableString("OK")
//                ok.setSpan(redColorSpan, 0, ok.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//                alertDialogBuilder.setMessage("Please enable location to continue")
//                alertDialogBuilder.setPositiveButton(ok) { dialog: DialogInterface, _: Int ->
//                    dialog.dismiss()
//                    val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                    startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE)
//                }
//                alertDialogBuilder.setCancelable(false)
//                alertDialogBuilder.show()
//            }
//        }
//    }

    private fun setObservers() {

        viewModel.performFetchShopsStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        isError = false
                        if (!binding.swipeRefreshLayout.isRefreshing) {
                            binding.layoutStates.visibility = View.VISIBLE
                            binding.animationView.visibility = View.GONE
                        }
                        errorSnackbar.dismiss()
                        progressDialog.setMessage("Getting Outlets")
                        progressDialog.show()
                    }

                    Resource.Status.EMPTY -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("empty_animation.json")
                        binding.animationView.playAnimation()
                        progressDialog.dismiss()
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        errorSnackbar.setText("No Outlets in this place")
                        Handler().postDelayed({ errorSnackbar.show() }, 500)
                    }

                    Resource.Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isError = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        progressDialog.dismiss()
                        errorSnackbar.dismiss()
                        shopList.clear()
                        it.data?.let { it1 ->
                            for (shop in it1.data.shops){
                                if(shop.name != null && shop.opening_time != null){
                                    shopList.add(shop)
                                }
                            }
                        }
                        Log.e("All shops" , shopList.toString())
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
                        progressDialog.dismiss()
                        errorSnackbar.setText("No Internet Connection")
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        Handler().postDelayed({ errorSnackbar.show() }, 500)
                    }

                    Resource.Status.ERROR -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        progressDialog.dismiss()
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("order_failed_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackbar.setText("Something went wrong")
                        shopList.clear()
                        shopAdapter.notifyDataSetChanged()
                        Handler().postDelayed({ errorSnackbar.show() }, 500)
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
                if (contactUsItem.identifier == drawerItem.identifier) {
                    startActivity(Intent(applicationContext, ContactUsActivity::class.java))
                }
                if (signOutItem.identifier == drawerItem.identifier) {
                    MaterialAlertDialogBuilder(this@HomeActivity)
                        .setTitle("Confirm Sign Out")
                        .setMessage("Are you sure want to sign out?")
                        .setPositiveButton("Yes") { _, _ ->

                            //TODO FireBase Notifications
                            preferencesHelper.clearPreferences()
                            viewModel.change(1)
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
                R.id.text_search -> {
                    startActivity(Intent(applicationContext, SearchActivity::class.java))
                }
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

    fun getCart(): ArrayList<Item> {
        val items: ArrayList<Item> = ArrayList()
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
            in 0..11 -> message = "Hey,\n"
            in 12..15 -> message = "Hey,\n"
            in 16..23 -> message = "Hey,\n"
        }
        var temp = preferencesHelper.name
        if(temp==null){
            temp="Sir"
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
        var total = 0.0
        var totalItems = 0
        if (cartList.size > 0) {
            for (i in cartList.indices) {
                total += cartList[i].variants[0].price * cartList[i].quantity
                totalItems += 1
            }
            if (totalItems == 1) {
                cartSnackBar.setText("₹$total | $totalItems item")
            } else {
                cartSnackBar.setText("₹$total | $totalItems items")
            }
            cartSnackBar.show()
        } else {
            cartSnackBar.dismiss()
        }
    }

    private fun updateHeaderLayoutUI() {
        headerLayout.textCustomerName.text = preferencesHelper.name
        headerLayout.textEmail.text = preferencesHelper.email
        val letter = preferencesHelper.name?.get(0).toString()
        val textDrawable = TextDrawable.builder()
            .buildRound(letter, ContextCompat.getColor(this, R.color.accent))
        headerLayout.imageProfilePic.setImageDrawable(textDrawable)
    }
}