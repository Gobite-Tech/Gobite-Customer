package com.example.gobitecustomer.ui.placeorder

import android.animation.LayoutTransition
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.UpdateStatusModel
import com.example.gobitecustomer.data.modelNew.sendOtpModel
import com.example.gobitecustomer.databinding.ActivityPlaceOrderBinding
import com.example.gobitecustomer.ui.order.OrderDetailActivity
import com.example.gobitecustomer.ui.home.HomeActivity
import com.example.gobitecustomer.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceOrderBinding
    private val viewModel by viewModel<PlaceOrderViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private var orderId: String? = null
    private var isOrderPlaced = false
    private var isOrderFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_order)

        getArgs()
        initView()
        setListener()
        setObservers()
        orderId?.let {
            viewModel.placeOrder(it , UpdateStatusModel("placed"))
        } ?: run {
            binding.animationView.loop(false)
            binding.animationView.setAnimation("order_failed_animation.json")
        }
    }

    private fun getArgs() {
        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_order)
        binding.layoutState.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
    }

    private fun setListener() {
        binding.textGoHome.setOnClickListener {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
        binding.textViewOrder.setOnClickListener {
            val i = Intent(applicationContext, HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            val j = Intent(applicationContext, OrderDetailActivity::class.java)
            j.putExtra(AppConstants.ORDER_ID,orderId)
            j.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(j)
        }
    }

    private fun setObservers() {
        viewModel.placeOrderStatus.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Resource.Status.LOADING -> {
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("loading_animation.json")
                    binding.animationView.playAnimation()
                    binding.textPlaceOrderStatus.text = "Placing order"
                    isOrderFailed = true
                }
                Resource.Status.SUCCESS -> {
                    binding.animationView.loop(false)
                    binding.animationView.setAnimation("order_success_animation.json")
                    binding.animationView.playAnimation()
                    binding.textPlaceOrderStatus.text = "Order placed successfully"
                    isOrderPlaced = true
                    isOrderFailed = false
                    viewModel.change(1)
                    sendOtp(preferencesHelper.mobile.toString())
                    preferencesHelper.clearCartPreferences()
                    binding.layoutRedirection.visibility = View.VISIBLE
                }
                Resource.Status.OFFLINE_ERROR -> {
                    binding.animationView.loop(false)
                    binding.animationView.setAnimation("no_internet_connection_animation.json")
                    binding.animationView.playAnimation()
                    binding.textPlaceOrderStatus.text = "No internet connection"
                    isOrderFailed = true
                }
                Resource.Status.ERROR -> {
                    binding.animationView.loop(false)
                    binding.animationView.setAnimation("order_failed_animation.json")
                    binding.animationView.playAnimation()
                    binding.textPlaceOrderStatus.text = "Something went wrong. Please try again after some time!"
                    isOrderFailed = true
                }

                else -> {}
            }
        })
    }

    override fun onBackPressed() {
        if (isOrderPlaced || isOrderFailed) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    private fun sendOtp(number: String) {
        val sendOtpModel = sendOtpModel(
            from = "Blueve",
            to = ArrayList<String>().apply { add(number) },
            type = "sms",
            type_details = "",
            data_coding = "plain",
            flash_message = false,
            campaign_id = "5622674",
            template_id = "767168465",
            validity = "30"
        )

        viewModel.sendOTP(sendOtpModel)
    }
}