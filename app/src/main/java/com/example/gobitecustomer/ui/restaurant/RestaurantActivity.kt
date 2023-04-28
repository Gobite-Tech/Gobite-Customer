package com.example.gobitecustomer.ui.restaurant

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.databinding.ActivityRestaurantBinding
import com.example.gobitecustomer.utils.AppConstants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class RestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantBinding
    private val viewModel by viewModel<RestaurantViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
//    private lateinit var foodAdapter: FoodAdapter
    private lateinit var progressDialog: ProgressDialog
//    var foodItemList: ArrayList<MenuItemModel> = ArrayList()
//    var cartList: ArrayList<MenuItemModel> = ArrayList()
//    var shop: shops? = null
//    var itemId = -1
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackBar: Snackbar
    private lateinit var closedSnackBar: Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        getArgs()
    }


    private fun getArgs() {
        val temp = intent.getStringExtra(AppConstants.SHOP)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant)
        setSupportActionBar(binding.toolbar)
//        shop = Gson().fromJson(temp, shops::class.java)
//        itemId = intent.getIntExtra(AppConstants.ITEM_ID, -1)
    }

}