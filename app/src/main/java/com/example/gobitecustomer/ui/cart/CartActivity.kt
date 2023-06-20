package com.example.gobitecustomer.ui.cart

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
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
import com.example.gobitecustomer.data.modelNew.PlaceOrderRequest
import com.example.gobitecustomer.data.modelNew.cartItem
import com.example.gobitecustomer.data.modelNew.shops
import com.example.gobitecustomer.databinding.ActivityCartBinding
import com.example.gobitecustomer.databinding.BottomSheetShopInfoBinding
import com.example.gobitecustomer.ui.payment.PaymentActivity
import com.example.gobitecustomer.utils.AppConstants
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding

    private val viewModel by viewModel<CartViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()

    private lateinit var cartAdapter: CartAdapter
    private lateinit var progressDialog: ProgressDialog
    private var cartList: MutableList<Item> = ArrayList()
    private var shop: shops? = null
    private lateinit var snackBar: Snackbar
    private lateinit var errorSnackBar: Snackbar
    private var isPickup = true

    private lateinit var placeOrderRequest: PlaceOrderRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        getArgs()
        initView()
        setListeners()
        setObservers()
    }

    private fun getArgs() {
        shop = preferencesHelper.getCartShop()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart)
        snackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        snackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button =
            errorSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        setSupportActionBar(binding.toolbar)
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
            if (abs(verticalOffset) - appBarLayout.totalScrollRange == 0) { //Collapsed
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp)
            } else { //Expanded
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
            }
        })
        setupMenuRecyclerView()
        updateShopUI()
    }

    @SuppressLint("SetTextI18n")
    private fun setListeners() {
        snackBar.setAction("Place Order") {
            if (cart.isEmpty()) {
                Toast.makeText(applicationContext, "Cart is empty", Toast.LENGTH_SHORT).show()
            } else {
                if(shop?.open_now == true){
                    showOrderConfirmation()
                }else{
                    Toast.makeText(applicationContext, "Not taking orders", Toast.LENGTH_SHORT).show()
                }
            }
        }
        errorSnackBar.setAction("Try again") {
            viewModel.verifyOrder(placeOrderRequest)
        }

        binding.textInfo.setOnClickListener {
            val dialogBinding: BottomSheetShopInfoBinding =
                DataBindingUtil.inflate(
                    layoutInflater,
                    R.layout.bottom_sheet_shop_info,
                    null,
                    false
                )
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(dialogBinding.root)
            dialog.show()
            dialogBinding.editTextInfo.setText(preferencesHelper.cartShopInfo)
            dialogBinding.buttonSaveTextInfo.setOnClickListener {
                preferencesHelper.cartShopInfo = dialogBinding.editTextInfo.text.toString()
                if (!preferencesHelper.cartShopInfo.isNullOrEmpty()) {
                    binding.textInfo.text = preferencesHelper.cartShopInfo
                } else {
                    binding.textInfo.text = "Any information to convey to " + shop?.name + "?"
                }
                dialog.dismiss()
            }
        }

    }

    private fun setObservers() {
        viewModel.insertOrderStatus.observe(this, Observer {
            when (it.status) {
                Resource.Status.LOADING -> {
                    errorSnackBar.dismiss()
                    progressDialog.setMessage("Verifying cart items...")
                    progressDialog.show()
                }

                Resource.Status.SUCCESS -> {
                    progressDialog.dismiss()
                    errorSnackBar.dismiss()
                    Toast.makeText(applicationContext, "Successfully Placed", Toast.LENGTH_SHORT).show()
                    snackBar.dismiss()
                    preferencesHelper.clearCartPreferences()
                    initiatePayment(it.data?.data?.order?.id.toString())
                }

                Resource.Status.OFFLINE_ERROR -> {
                    progressDialog.dismiss()
                    errorSnackBar.setText("No Internet Connection")
                    errorSnackBar.show()
                }

                Resource.Status.ERROR -> {
                    progressDialog.dismiss()
                    errorSnackBar.setText("Cart verification failed")
                    errorSnackBar.show()
                }

                else -> {}
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateShopUI() {
        Picasso.get().load(shop?.icon).placeholder(R.drawable.ic_shop).into(binding.layoutShop.imageShop)
        Picasso.get().load(shop?.coverurl).placeholder(R.drawable.shop_placeholder).into(binding.imageExpanded)
        binding.layoutShop.textShopName.text = shop?.name
//        binding.layoutShop.textShopRating.text = shop?.ratingModel?.rating.toString()
        if (!preferencesHelper.cartShopInfo.isNullOrEmpty()) {
            binding.textInfo.text = preferencesHelper.cartShopInfo
        } else {
            binding.textInfo.text = "Any information to convey to " + shop?.name + "?"
        }

    }

    private fun setupMenuRecyclerView() {
        cartList.clear()
        cartList.addAll(cart)
        updateCartUI()
        cartAdapter =
            CartAdapter(applicationContext, cartList, object : CartAdapter.OnItemClickListener {

                override fun onItemClick(item: Item?, position: Int) {
                    //TODO navigate to restaurant activity
                }

                override fun onQuantityAdd(position: Int) {
                    println("quantity add clicked $position")
                    cartList[position].quantity = cartList[position].quantity + 1
                    cartAdapter.notifyItemChanged(position)
                    updateCartUI()
                    saveCart(cartList)
                }

                override fun onQuantitySub(position: Int) {
                    println("quantity sub clicked $position")
                    if (cartList[position].quantity - 1 >= 0) {
                        cartList[position].quantity = cartList[position].quantity - 1
                        if (cartList[position].quantity == 0) {
                            cartList.removeAt(position)
                            cartAdapter.notifyDataSetChanged()
                        } else {
                            cartAdapter.notifyDataSetChanged()
                        }
                        updateCartUI()
                        saveCart(cartList)
                    }
                }
            })
        binding.recyclerFoodItems.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerFoodItems.adapter = cartAdapter
    }

    private var deliveryPrice = 0.0
    private var cartTotalPrice = 0.0
    val discont_value = 40.0

    @SuppressLint("SetTextI18n")
    private fun updateCartUI() {
        var total = 0.0
        var totalItems = 0
        if (cartList.size > 0) {

            binding.layoutContent.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            for (i in cartList.indices) {
                total += cartList[i].variants[0].price * cartList[i].quantity
                totalItems += 1
            }
//            if(!isPickup) {
//                total += deliveryPrice.toInt()
//                preferencesHelper.cartDeliveryPref = "delivery"
//            }

            Log.e("Cart Activity", "discount taken ${preferencesHelper.discount_taken}")
            if(preferencesHelper.discount_taken == 0){
                binding.discount.visibility = View.VISIBLE
                var discount = total*0.6
                discount = if(discount > discont_value){
                    discont_value
                }else{
                    total*0.6
                }
                binding.discountAmt.text = "$total - $discount = ${total - discount}"
                total -= discount
                Log.e("discount", "discount taken")
            }else{
                Log.e("discount", "discount not taken")
                binding.discount.visibility = View.GONE
            }

            binding.textTotal.text = "₹$total"
            if (totalItems == 1) {
                snackBar.setText("₹$total | $totalItems item")
            } else {
                snackBar.setText("₹$total | $totalItems items")
            }
            snackBar.show()
            cartTotalPrice = total
        } else {
            preferencesHelper.clearCartPreferences()
            snackBar.dismiss()
            binding.layoutContent.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        }
    }

    fun saveCart(foodItems: List<Item>?) {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val cartString = gson.toJson(foodItems)
        preferencesHelper.cart = cartString
    }

    val cart: List<Item>
        get() {
            val items: MutableList<Item> = ArrayList()
            val temp = preferencesHelper.getCart()
            if (temp != null) {
                items.addAll(temp)
            }
            return items
        }

    private fun initiatePayment(orderId: String) {
        val intent = Intent(applicationContext, PaymentActivity::class.java)
        intent.putExtra(AppConstants.ORDER_ID, orderId)
        preferencesHelper.shopMobile = shop?.mobile
        startActivity(intent)
        finish()
    }

    private fun verifyOrder() {
        var cookingInfo: String? = null
//        var deliveryLocation = ""
        if (!preferencesHelper.cartShopInfo.isNullOrEmpty()) {
            cookingInfo = preferencesHelper.cartShopInfo
        }
//        if(!preferencesHelper.cartDeliveryLocation.isNullOrEmpty()){
//            deliveryLocation = preferencesHelper.cartDeliveryLocation!!
//        }

        val listCartOrderItems: ArrayList<cartItem> = ArrayList()
        for (i in cart) {
            Log.e("cart", i.toString())
        }
        cart.forEach {
            listCartOrderItems.add(cartItem(it.id, it.variants[0].id, it.quantity))
        }
        placeOrderRequest = PlaceOrderRequest(listCartOrderItems)
        viewModel.verifyOrder(placeOrderRequest)
    }

    private fun showOrderConfirmation() {
        MaterialAlertDialogBuilder(this@CartActivity)
            .setTitle("Place order")
            .setCancelable(false)
            .setMessage("Are you sure want to place this order?")
            .setPositiveButton("Yes") { _, _ ->
                verifyOrder()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
                Handler().postDelayed({
                    snackBar.show()
                }, 500)
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
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