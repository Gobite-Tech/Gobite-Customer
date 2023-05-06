package com.example.gobitecustomer.ui.restaurant

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.Item
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ActivityRestaurantBinding
import com.example.gobitecustomer.ui.cart.CartActivity
import com.example.gobitecustomer.utils.AppConstants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class RestaurantActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantBinding
    private val viewModel by viewModel<RestaurantViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var progressDialog: ProgressDialog
    var foodItemList: ArrayList<Item> = ArrayList()
    var cartList: ArrayList<Item> = ArrayList()
    var shop: shops? = null
//    var itemId = -1
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackBar: Snackbar
    private lateinit var closedSnackBar: Snackbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        getArgs()
        initView()
        setObservers()
        cartSnackBar.setAction("View Cart") {
            startActivity(
                Intent(
                    applicationContext,
                    CartActivity::class.java
                )
            )
        }


        binding.imageCall.setOnClickListener {
            //TODO - Implement Mobile No of shops
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", "8965231478", null))
            startActivity(intent)
        }
    }


    private fun getArgs() {
        val temp = intent.getStringExtra(AppConstants.SHOP)
        setSupportActionBar(binding.toolbar)
        shop = Gson().fromJson(temp, shops::class.java)
//        itemId = intent.getIntExtra(AppConstants.ITEM_ID, -1)
    }
    var isShopOpen = true

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        progressDialog = ProgressDialog(this)
        cartSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        cartSnackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        closedSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val closedSnackButton: Button = closedSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        closedSnackButton.setCompoundDrawables(null, null, null, null)
        closedSnackButton.background = null
        closedSnackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        binding.toolbarLayout.setExpandedTitleColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.white
            )
        )
        binding.toolbarLayout.setCollapsedTitleTextColor(
            ContextCompat.getColor(
                applicationContext,
                android.R.color.black
            )
        )
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.appBar.post {
                if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //Collapsed
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_star,
                        0,
                        0,
                        0
                    )
                    binding.imageCall.setImageDrawable(getDrawable(R.drawable.ic_call_primary))
                    binding.textShopRating.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            android.R.color.black
                        )
                    )
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
                } else { //Expanded
                    binding.textShopRating.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_star_white,
                        0,
                        0,
                        0
                    )
                    binding.imageCall.setImageDrawable(getDrawable(R.drawable.ic_call_white))
                    binding.textShopRating.setTextColor(
                        ContextCompat.getColor(
                            applicationContext,
                            android.R.color.white
                        )
                    )
                    supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
                }
            }
        })
//        val openingTime = viewModel.getTime(shop?.shopModel?.openingTime)
//        val closingTime = viewModel.getTime(shop?.shopModel?.closingTime)
//        val currentTime = Date()
//        isShopOpen = currentTime.before(closingTime) && currentTime.after(openingTime)
//        if(isShopOpen){
//            isShopOpen = shop?.configurationModel?.isOrderTaken == 1
//        }
        setupMenuRecyclerView()
        updateShopUI()
    }

    private fun setObservers() {
        viewModel.performFetchMenuStatus.observe(this, Observer { resource ->
            when (resource.status) {
                Resource.Status.LOADING -> {
                    if (!binding.swipeRefreshLayout.isRefreshing) {
                        binding.layoutStates.visibility = View.VISIBLE
                        binding.animationView.visibility = View.GONE
                    }
                    errorSnackBar.dismiss()
                }
                Resource.Status.SUCCESS -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    cartList.clear()
                    cartList.addAll(cart)
                    updateCartUI()
                    foodItemList.clear()
                    resource.data?.let { it1 ->
                        it1.items.forEach { item ->
                            item.shop_id = shop?.id!!
//                            item.name = shop?.name.toString()
                            foodItemList.add(item)
                        }
                    }
                    Toast.makeText(applicationContext, "${foodItemList}", Toast.LENGTH_SHORT).show()
                    if (cartList.size > 0) {
                        if (cartList[0].shop_id == shop?.id) {
                            var i = 0
                            while (i < foodItemList.size) {
                                var j = 0
                                while (j < cartList.size) {
                                    if (cartList[j].id == foodItemList[i].id) {
                                        foodItemList[i].quantity = cartList[j].quantity
                                    }
                                    j++
                                }
                                i++
                            }
                        }
                    }
                    foodAdapter.notifyDataSetChanged()
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.GONE
                    binding.animationView.cancelAnimation()
                    //progressDialog.dismiss()
                    errorSnackBar.dismiss()
//                    highlightRedirectedItem()
                }
                Resource.Status.EMPTY -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("empty_animation.json")
                    binding.animationView.playAnimation()
                    //progressDialog.dismiss()
                    foodItemList.clear()
                    foodAdapter.notifyDataSetChanged()
                    errorSnackBar.setText("No food items available in this shop")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }
                Resource.Status.OFFLINE_ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("no_internet_connection_animation.json")
                    binding.animationView.playAnimation()
                    //progressDialog.dismiss()
                    errorSnackBar.setText("No Internet Connection")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }
                Resource.Status.ERROR -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.layoutStates.visibility = View.GONE
                    binding.animationView.visibility = View.VISIBLE
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("order_failed_animation.json")
                    binding.animationView.playAnimation()
                    //progressDialog.dismiss()
                    errorSnackBar.setText("Something went wrong")
                    Handler().postDelayed({ errorSnackBar.show() }, 500)
                }
            }
        })
    }

    private val cart: List<Item>
        get() {
            val items: MutableList<Item> = ArrayList()
            val temp = preferencesHelper.getCart()
            if (!temp.isNullOrEmpty()) {
                items.addAll(temp)
            }
            return items
        }

    private fun updateShopUI() {
        binding.toolbarLayout.title = shop?.name
        //TODO - implement Rating Model
        binding.textShopRating.text = "4.5"
    }


    lateinit var layoutManager: LinearLayoutManager
    private fun setupMenuRecyclerView() {
        foodAdapter =
            FoodAdapter(applicationContext, foodItemList, object : FoodAdapter.OnItemClickListener {
                override fun onItemClick(item: Item?, position: Int) {}
                override fun onQuantityAdd(position: Int) {
                    println("quantity add clicked $position")
                    if (cartList.size > 0) {
                        if (cartList[0].shop_id == shop?.id) {
                            foodItemList[position].quantity = foodItemList[position].quantity + 1
                            var k = 0
                            for (i in cartList.indices) {
                                if (cartList[i].id == foodItemList[position].id) {
                                    cartList[i] = foodItemList[position]
                                    k = 1
                                    break
                                }
                            }
                            if (k == 0) {
                                cartList.add(foodItemList[position])
                            }
                            foodAdapter.notifyItemChanged(position)
                            updateCartUI()
                            saveCart(cartList)
                        } else { //Show replace cart confirmation dialog
                            var message =
                                "Your cart contains food from " + cartList[0].name + ". "
                            message += "Do you want to discard the cart and add food from " + shop?.name + "?"
                            MaterialAlertDialogBuilder(this@RestaurantActivity)
                                .setTitle("Replace cart?")
                                .setMessage(message)
                                .setPositiveButton("Yes") { dialog, _ ->
                                    preferencesHelper.clearCartPreferences()
                                    foodItemList[position].quantity =
                                        foodItemList[position].quantity + 1
                                    foodAdapter.notifyItemChanged(position)
                                    cartList.clear()
                                    cartList.add(foodItemList[position])
                                    saveCart(cartList)
                                    updateCartUI()
                                    dialog.dismiss()
                                }
                                .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                                .show()
                        }
                    } else {
                        foodItemList[position].quantity = foodItemList[position].quantity + 1
                        cartList.add(foodItemList[position])
                        foodAdapter.notifyItemChanged(position)
                        updateCartUI()
                        saveCart(cartList)
                    }
                }

                override fun onQuantitySub(position: Int) {
                    println("quantity sub clicked $position")
                    if (foodItemList[position].quantity - 1 >= 0) {
                        foodItemList[position].quantity = foodItemList[position].quantity - 1
                        for (i in cartList.indices) {
                            if (cartList[i].id == foodItemList[position].id) {
                                if (foodItemList[position].quantity == 0) {
                                    cartList.removeAt(i)
                                } else {
                                    cartList[i] = foodItemList[position]
                                }
                                break
                            }
                        }
                        foodAdapter.notifyItemChanged(position)
                        updateCartUI()
                        saveCart(cartList)
                    }
                }
            }, isShopOpen)
        layoutManager =
            LinearLayoutManager(this@RestaurantActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerFoodItems.layoutManager = layoutManager
        binding.recyclerFoodItems.adapter = AlphaInAnimationAdapter(foodAdapter)
    }

    fun saveCart(foodItems: List<Item>) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val cartString = gson.toJson(foodItems)
        if (foodItems.isNotEmpty()) {
            preferencesHelper.cart = cartString
            preferencesHelper.cartShop = gson.toJson(shop)
        } else {
            preferencesHelper.cart = ""
            preferencesHelper.cartShop = ""
        }
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
//            if (shop?.configurationModel?.isOrderTaken == 1)   //Code Commented Due to unavailable fields in response
                cartSnackBar.show()
        } else {
            preferencesHelper.clearCartPreferences()
            cartSnackBar.dismiss()
        }


        //Code Commented Due to unavailable fields in response
//        if (shop?.configurationModel?.isOrderTaken == 1) {
//            if (shop?.configurationModel?.isDeliveryAvailable == 1) {
//                //supportActionBar?.subtitle = "Open now"
//                //binding.textPickupOnly.visibility = View.GONE
//                closedSnackBar.dismiss()
//            } else {
        binding.textPickupOnly.text = "Pick up only"
        binding.textPickupOnly.visibility = View.VISIBLE
//            }
//        } else {
//            cartSnackBar.dismiss()
//            closedSnackBar.setText("Not taking orders")
//            closedSnackBar.duration = Snackbar.LENGTH_LONG
//            closedSnackBar.show()
        //binding.textPickupOnly.text = "Not taking orders"
        //binding.textPickupOnly.visibility = View.VISIBLE
//        }
    }

    override fun onResume() {
        super.onResume()
        cartList.clear()
        cartList.addAll(cart)
        updateCartUI()
        foodItemList.clear()
        foodAdapter.notifyDataSetChanged()
        viewModel.getMenu(shop?.id.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

}