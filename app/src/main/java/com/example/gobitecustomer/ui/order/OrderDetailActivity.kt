package com.example.gobitecustomer.ui.order

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.Item
import com.example.gobitecustomer.data.modelNew.ItemXX
import com.example.gobitecustomer.data.modelNew.NotificationModel
import com.example.gobitecustomer.data.modelNew.OrderX
import com.example.gobitecustomer.data.modelNew.variantsItem
import com.example.gobitecustomer.databinding.ActivityOrderDetailsBinding
import com.example.gobitecustomer.ui.cart.CartActivity
import com.example.gobitecustomer.ui.restaurant.RestaurantActivity
import com.example.gobitecustomer.utils.AppConstants
import com.example.gobitecustomer.utils.EventBus
import com.example.gobitecustomer.utils.StatusHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.Exception
import java.text.SimpleDateFormat

class OrderDetailActivity : AppCompatActivity() ,  View.OnClickListener  {

    private lateinit var binding: ActivityOrderDetailsBinding
    private val viewModel by viewModel<OrderViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var orderTimelineAdapter: OrderTimelineAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<ItemXX> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private lateinit var order: OrderX
    private var orderId: String? = null
    var isPickup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        getArgs()
        initView()
        setListeners()
        setObservers()
        subscribeToOrderStatus()
    }

    private fun getArgs() {
        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
        if (orderId.isNullOrEmpty()) {
            order = Gson().fromJson(intent.getStringExtra(AppConstants.ORDER_DETAIL), OrderX::class.java)
        } else {
            viewModel.getOrderById(orderId!!)
        }
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        setupShopRecyclerView()
        setupOrderStatusRecyclerView()
        updateUI()

    }

    private fun updateUI() {

        preferencesHelper.getShopList()?.forEach { shop ->
            if (shop.id == order.shop_id) {
                binding.textShopName.text = shop.name
            }
        }
        try {
            val apiDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val appDateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm aaa")
            val date = apiDateFormat.parse(order.order_placed_time)
            val dateString = appDateFormat.format(date)
            binding.textOrderTime.text = dateString
            Log.e("OrderDetailActivity", "Done parsing date - $dateString")
        } catch (e: Exception) {
            Log.e("OrderDetailActivity", "Error parsing date - $e")
            e.printStackTrace()
        }
        //binding.textOrderPrice.text = "₹ " + order.transactionModel.orderModel.price.toInt().toString()
        binding.textSecretKey.text = order.id
//        Picasso.get().load(order.transactionModel.orderModel.shopModel?.photoUrl).placeholder(R.drawable.ic_shop).into(binding.imageShop)
        //binding.titleOrderStatus.text = StatusHelper.getStatusDetailedMessage(order.transactionModel.orderModel.orderStatus)
        binding.titleOrderStatus.text = "Track your order"
//        binding.textOrderId.text = "#" + order.transactionModel.orderModel.id
//        binding.textTransactionId.text = "#" + order.transactionModel.transactionId
        binding.textTotalPrice.text = "₹" + order.price.toInt().toString()
        binding.textPaymentMode.text = "Paid via Cash"
//        if (!order.transactionModel.orderModel.cookingInfo.isNullOrEmpty()) {
//            binding.textInfo.text = order.transactionModel.orderModel.cookingInfo
//        } else {
            binding.textInfo.visibility = View.GONE
//        }

        isPickup = true
        var itemTotal = 0.0
        order.items.forEach {
            itemTotal += it.total_price
        }
        binding.textItemTotalPrice.text = "₹" + itemTotal.toInt().toString()

        binding.layoutDeliveryCharge.visibility = View.GONE

        val orderStatus = order.order_status
//        val orderStatusModel = order.orderStatusModel.lastOrNull()
        when (orderStatus) {
            AppConstants.ORDER_STATUS_PREPARED-> {
                binding.textCancelReorder.visibility = View.VISIBLE
                binding.textRate.isEnabled = true
                binding.textCancelReorder.isEnabled = true
                binding.textCancelReorder.text = "REORDER"
            }

            AppConstants.ORDER_STATUS_CANCELLED -> {
                binding.textRate.isEnabled = true
                binding.textCancelReorder.isEnabled = false
                binding.textRate.visibility = View.VISIBLE
                binding.textCancelReorder.visibility = View.GONE
                binding.textRate.text = "REORDER"
            }

            AppConstants.ORDER_STATUS_CREATED -> {
                binding.textRate.visibility = View.GONE
                binding.textRate.isEnabled = false
                binding.textCancelReorder.visibility = View.VISIBLE
                binding.textCancelReorder.isEnabled = true
                binding.textCancelReorder.text = "CANCEL"
            }

            else -> {
                binding.textRate.visibility = View.GONE
                binding.textCancelReorder.visibility = View.GONE
            }
        }

//        if (order.transactionModel.orderModel.rating != null) {
//            if (order.transactionModel.orderModel.rating!! > 0.0) {
//                binding.layoutRating.visibility = View.VISIBLE
//                binding.textRating.text = order.transactionModel.orderModel.rating.toString()
//                binding.textRate.visibility = View.GONE
//            }else{
//                binding.layoutRating.visibility = View.GONE
//            }
//        }else{
//            binding.layoutRating.visibility = View.GONE
//        }
//        if(!order.transactionModel.orderModel.feedback.isNullOrEmpty()){
//            binding.textRatingFeedback.visibility = View.VISIBLE
//            order.transactionModel.orderModel.feedback?.let{
//                binding.textRatingFeedback.text = it
//            }
//        }else{
//            binding.textRatingFeedback.visibility = View.GONE
//        }

        when (orderStatus) {
            AppConstants.ORDER_STATUS_CREATED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CREATED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_BEING_PREPARED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PREPARED), is_updated = order.updated_at))

                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_CANCELLED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CREATED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CANCELLED), is_updated = order.updated_at))
                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_PLACED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CREATED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_BEING_PREPARED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PREPARED), is_updated = order.updated_at))

                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_BEING_PREPARED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CREATED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_BEING_PREPARED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PREPARED), is_updated = order.updated_at))

                orderTimelineAdapter.notifyDataSetChanged()
            }
            AppConstants.ORDER_STATUS_PREPARED -> {
                orderStatusList.clear()
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_CREATED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PLACED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = false, isDone = true, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_BEING_PREPARED), is_updated = order.updated_at))
                orderStatusList.add(OrderStatus(isCurrent = true, isDone = false, name = StatusHelper.getStatusMessage(AppConstants.ORDER_STATUS_PREPARED), is_updated = order.updated_at))

                orderTimelineAdapter.notifyDataSetChanged()
            }

        }
        Log.e("OrderDetailActivity", orderStatusList.toString())
    }

    private fun setListeners() {
        errorSnackBar.setAction("Try again") {

        }
        binding.imageRefresh.setOnClickListener {
            order.id.let { it1 -> viewModel.getOrderById(it1) }
        }
        binding.textCancelReorder.setOnClickListener {
            if (binding.textCancelReorder.text.toString().toUpperCase() != "REORDER") {
                showCancelOrderDialog()
            } else {
                //REORDER (Add items to cart)
                val cartItems = preferencesHelper.getCart()
                if (cartItems.isNullOrEmpty()) {
                    reOrder()
                } else {
                    MaterialAlertDialogBuilder(this@OrderDetailActivity)
                        .setTitle("Replace cart?")
                        .setMessage("Cart already contains some items. Are you sure want to replace the cart with this order items?")
                        .setPositiveButton("Yes") { dialog, _ ->
                            preferencesHelper.clearCartPreferences()
                            reOrder()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                }

            }
        }
    }

    private fun setObservers() {
        viewModel.orderByIdResponse.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        binding.layoutContent.visibility = View.GONE
                        binding.layoutShop.visibility = View.GONE
                        progressDialog.setMessage("Please wait...")
                        errorSnackBar.dismiss()
                        progressDialog.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        errorSnackBar.dismiss()
                        it.data?.let { orderItemListModel ->
                            order = orderItemListModel
                            updateUI()
                            orderList.clear()
                            orderList.addAll(order.items)
                            orderAdapter.notifyDataSetChanged()
                        }
                        binding.layoutContent.visibility = View.VISIBLE
                        binding.layoutShop.visibility = View.VISIBLE
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        errorSnackBar.setText("No Internet Connection")
                        errorSnackBar.show()

                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        errorSnackBar.setText("Something went wrong")
                        errorSnackBar.show()
                    }

                    else -> {}
                }
            }
        })
        viewModel.cancelOrderStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Cancelling order...")
                        errorSnackBar.dismiss()
                        progressDialog.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        errorSnackBar.dismiss()
                        order.order_status = AppConstants.ORDER_STATUS_CANCELLED
                        updateUI()
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        errorSnackBar.setText("No Internet Connection")
                        errorSnackBar.show()

                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        errorSnackBar.setText("Something went wrong")
                        errorSnackBar.show()
                    }

                    else -> {}
                }
            }
        })
    }

    private fun showCancelOrderDialog() {
        MaterialAlertDialogBuilder(this@OrderDetailActivity)
            .setTitle("Cancel order")
            .setMessage("Are you sure want to cancel this order?")
            .setPositiveButton("Yes") { dialog, _ ->
                val orderId = order.id
                viewModel.cancelOrder(orderId)
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun reOrder() {
        var cartString = ""
        var cartShop = ""
        val shopList = preferencesHelper.getShopList()
        if (shopList != null) {
            for (i in shopList) {
                if (i.id == order.shop_id) {
                    cartShop = Gson().toJson(i)
                }
            }
        }

        val cartList: ArrayList<Item> = ArrayList()
        order.items.forEach {
            cartList.add(
                Item(
                    id = it.id,
                    created_at = order.created_at,
                    quantity = it.quantity,
                    name = it.item_name,
                    variants = arrayListOf(variantsItem(
                        id = it.item_variant_id,
                        price = it.actual_price,
                        value = it.item_variant_value,
                        availability = 1,
                        reference_id = "Good"
                    )),
                    cover_photos = emptyList(),
                    category = "",
                    description = "",
                    filterable_fields = emptyList(),
                    icon = "",
                    item_type = "",
                    status = "",
                    updated_at = "",
                    shop_id = order.shop_id
                )
            )
        }
        cartString = Gson().toJson(cartList)
        preferencesHelper.cart = cartString
        preferencesHelper.cartShop = cartShop

        val j = Intent(applicationContext, CartActivity::class.java)
        j.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(j)
    }


    private fun setupShopRecyclerView() {
        if(orderId.isNullOrEmpty()){
            orderList.clear()
            orderList.addAll(order.items)
        }
        orderAdapter = OrderItemAdapter(applicationContext, orderList, object : OrderItemAdapter.OnItemClickListener {
            override fun onItemClick(item: ItemXX?, position: Int) {
                val intent = Intent(applicationContext, RestaurantActivity::class.java)
                intent.putExtra(AppConstants.SHOP, Gson().toJson(item))
                startActivity(intent)
            }
        })
        binding.recyclerOrderItems.layoutManager = LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerOrderItems.adapter = orderAdapter
    }

    var orderStatusList: ArrayList<OrderStatus> = ArrayList()
    private fun setupOrderStatusRecyclerView() {
        orderTimelineAdapter = OrderTimelineAdapter(applicationContext, orderStatusList, object : OrderTimelineAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderStatus?, position: Int) {}
        })
        binding.recyclerStatus.layoutManager = LinearLayoutManager(this@OrderDetailActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerStatus.adapter = AlphaInAnimationAdapter(orderTimelineAdapter)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_close -> {
                onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    @ExperimentalCoroutinesApi
    private fun subscribeToOrderStatus() {
        val subscription = EventBus.asFlow<NotificationModel>()
        CoroutineScope(Dispatchers.Main).launch {
            subscription.collect {
                println("Received order status event")
                val payload = it.payload
                if (payload.has("orderId")) {
                    val orderItemId = payload.getString("orderId")
                    if(order.id== orderItemId){
                        viewModel.getOrderById(orderItemId,true)
                    }
                }
            }
        }
    }

}