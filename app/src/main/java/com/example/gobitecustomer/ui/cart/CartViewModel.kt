package com.example.gobitecustomer.ui.cart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.PlaceOrderRequest
import com.example.gobitecustomer.data.modelNew.VerifyOrderResponse
import com.example.gobitecustomer.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class CartViewModel(
    private val orderRepository: OrderRepository
    ) : ViewModel() {

    //verify cart items
    private val insertOrder = MutableLiveData<Resource<VerifyOrderResponse>>()
    val insertOrderStatus: LiveData<Resource<VerifyOrderResponse>>
        get() = insertOrder

    fun verifyOrder(placeOrderRequest: PlaceOrderRequest) {
        viewModelScope.launch {
            try {
                insertOrder.value = Resource.loading()
                val response = orderRepository.insertOrder(placeOrderRequest)
                if (response != null) {
                    if (response.isSuccessful) {
                        insertOrder.value = Resource.success(response.body()!!)
                    } else {
                        insertOrder.value = Resource.error(null, response.message())
                    }
                }
            } catch (e: Exception) {
                Log.e("verify order failed", "${e.message}")
                if (e is UnknownHostException) {
                    insertOrder.value = Resource.offlineError()
                } else {
                    insertOrder.value = Resource.error(e)
                }
            }
        }
    }

}