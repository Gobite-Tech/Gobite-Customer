package com.example.gobitecustomer.ui.order

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.modelNew.OrderItemListModel
import com.example.gobitecustomer.databinding.ActivityOrderDetailsBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private val viewModel by viewModel<OrderViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var orderAdapter: OrderItemAdapter
    private lateinit var orderTimelineAdapter: OrderTimelineAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderItems> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private lateinit var order: OrderItemListModel
    private var orderId: String? = null
    var isPickup = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
    }
}