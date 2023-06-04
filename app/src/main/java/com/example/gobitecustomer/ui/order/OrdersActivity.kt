package com.example.gobitecustomer.ui.order

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gobitecustomer.R
import com.example.gobitecustomer.data.local.PreferencesHelper
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.NotificationModel
import com.example.gobitecustomer.data.modelNew.OrderX
import com.example.gobitecustomer.databinding.ActivityOrdersBinding
import com.example.gobitecustomer.utils.AppConstants
import com.example.gobitecustomer.utils.EventBus
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Timer
import java.util.TimerTask

class OrdersActivity : AppCompatActivity(), View.OnClickListener  {

    private lateinit var binding: ActivityOrdersBinding
    private val viewModel by viewModel<OrderViewModel>()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var orderAdapter: OrdersAdapter
    private lateinit var progressDialog: ProgressDialog
    private var orderList: ArrayList<OrderX> = ArrayList()
    private lateinit var errorSnackBar: Snackbar
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        initView()
        setListeners()
        setObservers()
        errorSnackBar.setAction("Try again") {
            getOrders()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            page = 1
            isFirstTime = true
            isLoading = false
            isLastPage = false
            getOrders()
        }
//        subscribeToOrderStatus()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_orders)
        binding.imageClose.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        errorSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackBar.view.findViewById(com.mikepenz.materialize.R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        val text = "<font color=#000000>Manage and track<br>your </font> <font color=#FF4141>orders</font>"
        binding.titleOrders.text = Html.fromHtml(text)
        //binding.layoutSearch.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        setupShopRecyclerView()
    }

    private fun setListeners() {
        binding.editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (s.toString().length > 2) {
                            //viewModel.getMenu(preferencesHelper.getPlace()?.id.toString(),s.toString())
                        } else {
                            runOnUiThread {
                                orderList.clear()
                                orderAdapter.notifyDataSetChanged()
                                binding.appBarLayout.setExpanded(true, true)
                            }
                        }
                    }
                }, 600)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                timer?.cancel()
            }
        })
    }

    var isFirstTime = true
    private fun setObservers() {
        viewModel.performFetchOrdersStatus.observe(this, Observer { it ->
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        isLoading = true
                        if (isFirstTime) {
                            binding.layoutStates.visibility = View.VISIBLE
                            binding.animationView.visibility = View.GONE
                        }else{
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        errorSnackBar.dismiss()
                    }
                    Resource.Status.EMPTY -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isLoading = false
                        isLastPage = true
                        binding.progressBar.visibility = View.GONE
                        if(isFirstTime) {
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.VISIBLE
                            binding.animationView.loop(true)
                            binding.animationView.setAnimation("empty_animation.json")
                            binding.animationView.playAnimation()
                            orderList.clear()
                            orderAdapter.notifyDataSetChanged()
                            errorSnackBar.setText("No orders found")
                            Handler().postDelayed({ errorSnackBar.show() }, 500)
                        }
                        //binding.appBarLayout.setExpanded(true, true)
                    }
                    Resource.Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isLoading = false
                        binding.progressBar.visibility = View.GONE
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        errorSnackBar.dismiss()
                        if(isFirstTime) {
                            orderList.clear()
                        }
                        Log.e("size testing ", it.data?.size.toString())
                        it.data?.let { it1 -> orderList.addAll(it1) }
                        if (it.data.isNullOrEmpty()) {
                            isLastPage = true
                        } else {
                            isLastPage = it.data.size < 10
                            if(!isLastPage) page += 1
                        }
                        val list = preferencesHelper.getShopList()
                        orderList.forEach {or ->
                            if (list != null) {
                                for (shop in list){
                                    if(shop.id == or.shop_id){
                                        or.shop_name = shop.name
                                    }
                                }
                            }
                            }
                        orderList.reverse()
                        orderAdapter.notifyDataSetChanged()
                        isFirstTime = false
                        //binding.appBarLayout.setExpanded(false, true)
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isLoading = false
                        binding.progressBar.visibility = View.GONE
                        if(isFirstTime) {
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.VISIBLE
                            binding.animationView.loop(true)
                            binding.animationView.setAnimation("no_internet_connection_animation.json")
                            binding.animationView.playAnimation()
                            errorSnackBar.setText("No Internet Connection")
                            Handler().postDelayed({ errorSnackBar.show() }, 500)
                        }
                        //binding.appBarLayout.setExpanded(true, true)

                    }
                    Resource.Status.ERROR -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isLoading = false
                        binding.progressBar.visibility = View.GONE
                        if(isFirstTime) {
                            binding.layoutStates.visibility = View.GONE
                            binding.animationView.visibility = View.VISIBLE
                            binding.animationView.loop(true)
                            binding.animationView.setAnimation("order_failed_animation.json")
                            binding.animationView.playAnimation()
                            errorSnackBar.setText("Something went wrong")
                            Handler().postDelayed({ errorSnackBar.show() }, 500)
                        }
                        //binding.appBarLayout.setExpanded(true, true)
                    }

                    else -> {}
                }
            }
        })

        //Rate Order
//        viewModel.rateOrderStatus.observe(this, Observer {
//            if (it != null) {
//                when (it.status) {
//                    Resource.Status.LOADING -> {
//                        progressDialog.setMessage("Please wait...")
//                        errorSnackBar.dismiss()
//                        progressDialog.show()
//                    }
//                    Resource.Status.SUCCESS -> {
//                        progressDialog.dismiss()
//                        errorSnackBar.dismiss()
//                        getOrders()
//                    }
//                    Resource.Status.OFFLINE_ERROR -> {
//                        progressDialog.dismiss()
//                        errorSnackBar.setText("No Internet Connection")
//                        errorSnackBar.show()
//
//                    }
//                    Resource.Status.ERROR -> {
//                        progressDialog.dismiss()
//                        errorSnackBar.setText("Something went wrong")
//                        errorSnackBar.show()
//                    }
//                }
//            }
//        })

    }

    var isLoading = false
    var isLastPage = false
    var page = 1
    private fun setupShopRecyclerView() {
        orderAdapter = OrdersAdapter(orderList, object : OrdersAdapter.OnItemClickListener {
            override fun onItemClick(item: OrderX, position: Int) {
                val intent = Intent(applicationContext, OrderDetailActivity::class.java)
                intent.putExtra(AppConstants.ORDER_DETAIL, Gson().toJson(item))
                startActivity(intent)
            }

            override fun onRatingClick(item: OrderX, position: Int) {
//                showRatingDialog(item)
            }
        })
        val layoutManager = LinearLayoutManager(this@OrdersActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.layoutManager = layoutManager
        binding.recyclerShops.adapter = AlphaInAnimationAdapter(orderAdapter)

        binding.recyclerShops.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && !isLastPage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 && totalItemCount >= 10) {
                        preferencesHelper.userId?.let {
                            viewModel.getOrders()
                        }
                    }
                }
            }
        })
    }


    private fun getOrders() {
        orderList.clear()
        orderAdapter.notifyDataSetChanged()
        //TODO pagination
        preferencesHelper.userId?.let {
            viewModel.getOrders()
        }
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
        page = 1
        isFirstTime = true
        isLoading = false
        isLastPage = false
        getOrders()
    }


//    private fun subscribeToOrderStatus() {
//        val subscription = EventBus.asFlow<NotificationModel>()
//        CoroutineScope(Dispatchers.Main).launch {
//            subscription.collect{
//                println("Received order status event")
//                page = 1
//                isFirstTime = true
//                isLoading = false
//                isLastPage = false
//                getOrders()
//            }
//        }
//    }
}