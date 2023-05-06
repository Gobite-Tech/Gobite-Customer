package com.example.gobitecustomer.ui.placeorder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gobitecustomer.data.local.Resource
import com.example.gobitecustomer.data.modelNew.VerifyOrderResponse
import com.example.gobitecustomer.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PlaceOrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    //place order
    private val placeOrder = MutableLiveData<Resource<VerifyOrderResponse>>()
    val placeOrderStatus: LiveData<Resource<VerifyOrderResponse>>
        get() = placeOrder

    fun placeOrder(orderId: String) {
        viewModelScope.launch {
            try {
                placeOrder.value = Resource.loading()
                val response = orderRepository.placeOrder(orderId)
                if(response!=null){
                    if(response.isSuccessful){
                        placeOrder.value = Resource.success(response.body()!!)
                    }else{
                        placeOrder.value = Resource.error(null,response.message())
                    }
                }
            } catch (e: Exception) {
                println("place order failed ${e.message}")
                if (e is UnknownHostException) {
                    placeOrder.value = Resource.offlineError()
                } else {
                    placeOrder.value = Resource.error(e)
                }
            }
        }
    }

}