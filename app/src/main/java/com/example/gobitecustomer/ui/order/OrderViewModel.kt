package com.example.gobitecustomer.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.OrderItemByIDModel
import com.example.gobitecustomer.data.modelNew.OrderItemListModel
import com.example.gobitecustomer.data.modelNew.OrderX
import com.example.gobitecustomer.data.modelNew.UpdateStatusModel
import com.example.gobitecustomer.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    //get order detail by order id
    private val orderByIdRequest = MutableLiveData<Resource<OrderX>>()
    val orderByIdResponse: LiveData<Resource<OrderX>>
        get() = orderByIdRequest

    fun getOrderById(orderId: String, isSilent: Boolean = false) {
        viewModelScope.launch {
            try {
                if(!isSilent) {
                    orderByIdRequest.value = Resource.loading()
                }
                val response = orderRepository.getOrderById(orderId)
                if (response.isSuccessful)
                    orderByIdRequest.value = Resource.success(response.body()?.data?.order!!)
                else {
                    orderByIdRequest.value = Resource.error(message = response.message())
                }

            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    orderByIdRequest.value = Resource.offlineError()
                } else {
                    orderByIdRequest.value = Resource.error(e)
                }
            }
        }
    }

    //fetch orders
    private val performFetchOrders = MutableLiveData<Resource<OrderItemListModel>>()
    val performFetchOrdersStatus: LiveData<Resource<OrderItemListModel>>
        get() = performFetchOrders

    fun getOrders(page_size : Int = 5 ,sort_order : String = "DESC" , nextPageToken: String?="") {
        viewModelScope.launch {
            try {
                performFetchOrders.value = Resource.loading()
                val response = orderRepository.getOrder(page_size,sort_order,nextPageToken!!)
                if (response.isSuccessful) {
                    if (response.body()?.data?.orders?.size!! > 0) {
                        performFetchOrders.value = Resource.success(response.body()!!)
                    } else {
                        performFetchOrders.value = Resource.empty()
                    }
                } else {
                    performFetchOrders.value = Resource.empty()
                }
            } catch (e: Exception) {
                println("fetch orders failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchOrders.value = Resource.offlineError()
                } else {
                    performFetchOrders.value = Resource.error(e)
                }
            }
        }
    }

    //cancel order
    private val cancelOrder = MutableLiveData<Resource<String>>()
    val cancelOrderStatus: LiveData<Resource<String>>
        get() = cancelOrder

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            try {
                cancelOrder.value = Resource.loading()
                val response = orderRepository.placeOrder(orderId , UpdateStatusModel("cancelled"))
                if (response.isSuccessful) {
                    cancelOrder.value = Resource.success("Order Removed")
                } else {
                    cancelOrder.value = Resource.error(null, response.message())
                }
            } catch (e: Exception) {
                Log.e("cancel order failed", "${e.message}")
                if (e is UnknownHostException) {
                    cancelOrder.value = Resource.offlineError()
                } else {
                    cancelOrder.value = Resource.error(e)
                }
            }
        }
    }

}